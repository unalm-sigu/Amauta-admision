package pe.edu.lamolina.amauta.controller.nivelacioneegg.matriculalote;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioCursoDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.AlumnoNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.NotaAlumnoNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoNivelacionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;

import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;

import pe.edu.lamolina.model.horario.HorarioCurso;
import pe.edu.lamolina.model.nivelacioneegg.AlumnoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class MatriculaLoteServiceImpl implements MatriculaLoteService {

    private final AlumnoNivelacionDAO alumnoNivelacionDAO;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final CursoNivelacionDAO cursoNivelacionDAO;
    private final HorarioCursoDAO horarioCursoDAO;
    private final NotaAlumnoNivelacionDAO notaAlumnoNivelacionDAO;

    private final VerificadorService verificadorService;

    private void verificarPermiso(DataSessionPivot ds) {
        boolean esOperador = verificadorService.esOperadorEEGG(ds);
        Assert.isTrue(esOperador, "No tiene permiso para ejecutar esta operación");
    }

    private static class TimeSlot {

        Date semana; // Fecha del lunes
        Long diaId;
        Long horaId;

        public TimeSlot(Date semana, Long diaId, Long horaId) {
            this.semana = semana;
            this.diaId = diaId;
            this.horaId = horaId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TimeSlot timeSlot = (TimeSlot) o;
            return Objects.equals(semana, timeSlot.semana)
                    && Objects.equals(diaId, timeSlot.diaId)
                    && Objects.equals(horaId, timeSlot.horaId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(semana, diaId, horaId);
        }
    }

    @Override
    @Transactional
    public int procesarMatriculaLote(CicloAcademico ciclo, DataSessionPivot ds) {
        this.verificarPermiso(ds);

        List<AlumnoNivelacion> alumnosAll = alumnoNivelacionDAO.allByCiclo(ciclo).stream()
                // .filter(an -> an.getAlumno().getCodigo().equals("20260002"))
                .collect(Collectors.toList());
        List<NotaAlumnoNivelacion> prematriculasAll = notaAlumnoNivelacionDAO.allByAlumnosNivelacion(alumnosAll);
        List<AlumnoNivelacion> alumnos = alumnosAll.stream()
                .filter(aluNiv -> {
                    List<NotaAlumnoNivelacion> prematriculas = prematriculasAll.stream()
                            .filter(pmat -> pmat.getAlumnoNivelacion().getId().equals(aluNiv.getId()))
                            .filter(pmat -> pmat.getCurso() != null)
                            .collect(Collectors.toList());
                    aluNiv.setNotasNivelaciones(prematriculas);
                    return prematriculas != null && !prematriculas.isEmpty();
                })
                .collect(Collectors.toList());

        // 1. PRE-OPTIMIZACIÓN: Cargar Horarios y Cursos en Memoria
        // Mapa: ID de CursoCiclo -> Lista de Secciones disponibles (CursoNivelacion)
        Map<Long, List<CursoNivelacion>> cursosDisponiblesMap = cargarCursosDisponiblesAgrupados(ciclo);

        // Mapa: ID de Plantilla -> Set de TimeSlots (para chequeo rápido de cruces)
        Map<String, Set<TimeSlot>> horariosMap = cargarMapaHorarios(ciclo);

        // 2. PROCESAR ALUMNOS
        int nuevos = 0;
        for (AlumnoNivelacion alumno : alumnos) {
            log.info("Procesando alumno {}", alumno.getAlumno().getCodigo());
            nuevos += matricularAlumno(alumno, cursosDisponiblesMap, horariosMap, ds) ? 1 : 0;
        }

        if (nuevos > 0) {
            CicloAcademico cicloBD = cicloAcademicoDAO.findByCiclo(ciclo);
            if (cicloBD.getFechaMatriculaNivelacion() == null) {
                cicloBD.setFechaMatriculaNivelacion(new Date());
                cicloAcademicoDAO.update(cicloBD);
            }
        }
        return nuevos;
    }

    private boolean matricularAlumno(AlumnoNivelacion alumno,
                                     Map<Long, List<CursoNivelacion>> cursosDisponiblesMap,
                                     Map<String, Set<TimeSlot>> horariosMap,
                                     DataSessionPivot ds) {

        // A. Identificar cursos YA matriculados (para evitar cruces con lo que ya tiene)
        Set<TimeSlot> slotsOcupados = new HashSet<>();
        List<NotaAlumnoNivelacion> cursosYaMatriculados = alumno.getNotasNivelaciones().stream()
                .filter(n -> n.getEstadoEnum() == MAT)
                .collect(Collectors.toList());
        log.info("** cursos ya matriculados {}", cursosYaMatriculados.size());

        for (NotaAlumnoNivelacion nota : cursosYaMatriculados) {
            Long gpoId = nota.getCursoNivelacion().getPlantilla().getId();
            Long cursoId = nota.getCursoNivelacion().getCursoCiclo().getCurso().getId();
            if (nota.getCursoNivelacion() != null && nota.getCursoNivelacion().getPlantilla() != null) {
                Set<TimeSlot> slots = horariosMap.get(gpoId + "-" + cursoId);
                if (slots != null) {
                    slotsOcupados.addAll(slots);
                }
            }
        }

        // B. Identificar cursos PENDIENTES (NMAT)
        List<NotaAlumnoNivelacion> cursosPendientes = alumno.getNotasNivelaciones().stream()
                .filter(n -> n.getEstadoEnum() == NMAT)
                .filter(n -> n.getEsMatriculable())
                .collect(Collectors.toList());
        log.info("** cursos pendientes {}", cursosPendientes.size());

        if (cursosPendientes.isEmpty()) {
            return false;
        }

        for (NotaAlumnoNivelacion nota : cursosPendientes) {
            log.info("** curso.codigo={} curso.id={}", nota.getCurso().getCodigo(), nota.getCurso().getId());
        }

        // C. Intentar encontrar una combinación válida (BACKTRACKING)
        Map<NotaAlumnoNivelacion, CursoNivelacion> solucion = new HashMap<>();

        boolean exito = buscarCombinacion(0, cursosPendientes, slotsOcupados, solucion, cursosDisponiblesMap, horariosMap);

        // D. Si hubo éxito, persistir cambios
        if (exito) {
            aplicarMatricula(alumno, solucion, ds);
        }
        return exito;
    }

    /**
     * Algoritmo recursivo para encontrar horarios que encajen
     */
    private boolean buscarCombinacion(int indice,
                                      List<NotaAlumnoNivelacion> pendientes,
                                      Set<TimeSlot> slotsOcupados,
                                      Map<NotaAlumnoNivelacion, CursoNivelacion> solucionActual,
                                      Map<Long, List<CursoNivelacion>> cursosDisponiblesMap,
                                      Map<String, Set<TimeSlot>> horariosMap) {

        // Caso Base: Hemos asignado curso a todas las notas pendientes
        if (indice == pendientes.size()) {
            log.info("*** finalizo busqueda de horarios");
            return true;
        }

        NotaAlumnoNivelacion notaActual = pendientes.get(indice);
        Long cursoId = notaActual.getCurso().getId();
        String codigo = notaActual.getCurso().getCodigo();
        log.info("*** barriendo secciones del curso {} indice {}", codigo, indice);

        // Obtener posibles secciones para esta materia
        List<CursoNivelacion> opciones = cursosDisponiblesMap.getOrDefault(cursoId, Collections.emptyList());
        log.info("**** curso.id={} opciones={}", cursoId, opciones.size());

        for (CursoNivelacion opcion : opciones) {
            // 1. Validar Vacantes (en tiempo real o memoria)
            if (opcion.getMatriculados() >= opcion.getVacantes()) {
                log.info("**** no hay vacantes en {}", opcion.getCodigo());
                continue;
            }

            // 2. Validar Cruce de Horarios
            Long plantillaId = opcion.getPlantilla().getId();
            Set<TimeSlot> slotsOpcion = horariosMap.get(plantillaId + "-" + cursoId);
            if (slotsOpcion != null && tieneCruce(slotsOcupados, slotsOpcion)) {
                log.info("**** hay cruce-horarios con {}", opcion.getCodigo());
                continue;
            }

            // 3. Si pasa validaciones, "Simular" inscripción
            solucionActual.put(notaActual, opcion);
            Set<TimeSlot> nuevosSlotsOcupados = new HashSet<>(slotsOcupados);
            if (slotsOpcion != null) {
                nuevosSlotsOcupados.addAll(slotsOpcion);
            }

            // Restar vacante temporalmente (lógica en memoria para la recursión, 
            // aunque lo ideal es solo chequear y actualizar al final para no afectar otros hilos si fuera paralelo)
            opcion.setMatriculados(opcion.getMatriculados() + 1);
            opcion.setDisponibles(opcion.getDisponibles() - 1);
            log.info("**** todo bien con la sección {}", opcion.getCodigo());

            // 4. Paso Recursivo: Intentar con el siguiente curso
            if (buscarCombinacion(indice + 1, pendientes, nuevosSlotsOcupados, solucionActual, cursosDisponiblesMap, horariosMap)) {
                return true; // Encontramos el camino feliz
            }

            // 5. Backtracking: Deshacer cambios si el camino no funcionó
            opcion.setMatriculados(opcion.getMatriculados() - 1); // Devolver vacante
            opcion.setDisponibles(opcion.getDisponibles() + 1); // Aumentar disponibles
            solucionActual.remove(notaActual);
            log.info("**** horario fallido, saliendo de la sección {}", opcion.getCodigo());
        }

        return false; // No se encontró ninguna opción válida para este nodo
    }

    private boolean tieneCruce(Set<TimeSlot> ocupados, Set<TimeSlot> nuevos) {
        // Collections.disjoint devuelve true si NO tienen elementos en común.
        // Queremos saber si SI tienen elementos en común.
        return !Collections.disjoint(ocupados, nuevos);
    }

    private void aplicarMatricula(AlumnoNivelacion alumno, Map<NotaAlumnoNivelacion, CursoNivelacion> solucion, DataSessionPivot ds) {
        // Actualizar Alumno
        alumno.setEstadoEnum(MAT); // Cambiamos estado general si la lógica de negocio lo dicta así
        alumno.setUserModificacion(ds.getUsuario());
        alumno.setFechaModificacion(new Date());

        for (Map.Entry<NotaAlumnoNivelacion, CursoNivelacion> entry : solucion.entrySet()) {
            NotaAlumnoNivelacion nota = entry.getKey();
            CursoNivelacion curso = entry.getValue();

            // Actualizar Nota
            nota.setEstadoEnum(MAT);
            nota.setCursoNivelacion(curso);
            nota.setUserModificacion(ds.getUsuario());
            nota.setFechaModificacion(new Date());

            // Actualizar Curso (Vacantes ya fueron "tocadas" en el objeto memoria durante backtracking,
            // pero asegúrate de que JPA detecte el cambio para el UPDATE en DB)
            // Nota: En el backtracking incrementamos matriculados. Al ser objetos referenciados 
            // y estar dentro de una transacción, el save del repositorio persistirá el +1.
            notaAlumnoNivelacionDAO.update(nota);

            curso.setUserModificacion(ds.getUsuario());
            curso.setFechaModificacion(new Date());
            cursoNivelacionDAO.update(curso);
        }
        alumnoNivelacionDAO.update(alumno);
    }

    private Map<Long, List<CursoNivelacion>> cargarCursosDisponiblesAgrupados(CicloAcademico ciclo) {
        // Traer todos los cursos con vacantes > matriculados
        // select c from CursoNivelacion c where c.matriculados < c.vacantes
        List<CursoNivelacion> cursos = cursoNivelacionDAO.allActivosByCiclo(ciclo);

        return cursos.stream()
                .filter(cn -> cn.getDisponibles() > 0)
                .collect(Collectors.groupingBy(c -> c.getCursoCiclo().getCurso().getId()));
    }

    private Map<String, Set<TimeSlot>> cargarMapaHorarios(CicloAcademico ciclo) {
        // Traer todos los horarios. Cuidado con la memoria si son millones.
        List<HorarioCurso> horarios = horarioCursoDAO.allByCiclo(ciclo);
        Map<String, Set<TimeSlot>> map = new HashMap<>();

        for (HorarioCurso h : horarios) {
            Long plntllaId = h.getPlantilla().getId();
            Long cursoId = h.getCursoCiclo().getCurso().getId();

            map.computeIfAbsent(plntllaId + "-" + cursoId, k -> new HashSet<>())
                    .add(new TimeSlot(h.getSemana(), h.getDia().getId(), h.getHora().getId()));
        }
        return map;
    }

    @Override
    @Transactional
    public int procesarMatriculaParcial(CicloAcademico ciclo, DataSessionPivot ds) {
        this.verificarPermiso(ds);

        List<AlumnoNivelacion> alumnosAll = alumnoNivelacionDAO.allByCiclo(ciclo);
        List<NotaAlumnoNivelacion> prematriculasAll = notaAlumnoNivelacionDAO.allByAlumnosNivelacion(alumnosAll);
        List<AlumnoNivelacion> alumnos = alumnosAll.stream()
                .filter(aluNiv -> {
                    List<NotaAlumnoNivelacion> prematriculas = prematriculasAll.stream()
                            .filter(pmat -> pmat.getAlumnoNivelacion().getId().equals(aluNiv.getId()))
                            .filter(pmat -> pmat.getCurso() != null)
                            .collect(Collectors.toList());
                    aluNiv.setNotasNivelaciones(prematriculas);
                    return prematriculas != null && !prematriculas.isEmpty();
                })
                .collect(Collectors.toList());

        // 1. PRE-OPTIMIZACIÓN: Cargar Horarios y Cursos en Memoria
        // Mapa: ID de CursoCiclo -> Lista de Secciones disponibles (CursoNivelacion)
        Map<Long, List<CursoNivelacion>> cursosDisponiblesMap = cargarCursosDisponiblesAgrupados(ciclo);

        // Mapa: ID de Plantilla -> Set de TimeSlots (para chequeo rápido de cruces)
        Map<String, Set<TimeSlot>> horariosMap = cargarMapaHorarios(ciclo);

        // 2. PROCESAR ALUMNOS
        int nuevos = 0;
        for (AlumnoNivelacion alumno : alumnos) {
            log.info("Procesando alumno {}", alumno.getAlumno().getCodigo());
            nuevos += matricularPartesAlumno(alumno, cursosDisponiblesMap, horariosMap, ds) ? 1 : 0;
        }
        return nuevos;
    }

    private boolean matricularPartesAlumno(AlumnoNivelacion alumno,
                                     Map<Long, List<CursoNivelacion>> cursosDisponiblesMap,
                                     Map<String, Set<TimeSlot>> horariosMap,
                                     DataSessionPivot ds) {

        // A. Identificar cursos YA matriculados (para evitar cruces con lo que ya tiene)
        Set<TimeSlot> slotsOcupados = new HashSet<>();
        List<NotaAlumnoNivelacion> cursosYaMatriculados = alumno.getNotasNivelaciones().stream()
                .filter(n -> n.getEstadoEnum() == MAT)
                .collect(Collectors.toList());
        log.info("** cursos ya matriculados {}", cursosYaMatriculados.size());

        for (NotaAlumnoNivelacion nota : cursosYaMatriculados) {
            Long gpoId = nota.getCursoNivelacion().getPlantilla().getId();
            Long cursoId = nota.getCursoNivelacion().getCursoCiclo().getCurso().getId();
            if (nota.getCursoNivelacion() != null && nota.getCursoNivelacion().getPlantilla() != null) {
                Set<TimeSlot> slots = horariosMap.get(gpoId + "-" + cursoId);
                if (slots != null) {
                    slotsOcupados.addAll(slots);
                }
            }
        }

        // B. Identificar cursos PENDIENTES (NMAT)
        List<NotaAlumnoNivelacion> cursosPendientes = alumno.getNotasNivelaciones().stream()
                .filter(n -> n.getEstadoEnum() == NMAT)
                .filter(n -> n.getEsMatriculable())
                .collect(Collectors.toList());
        log.info("** cursos pendientes {}", cursosPendientes.size());

        if (cursosPendientes.isEmpty()) {
            return false;
        }

        for (NotaAlumnoNivelacion nota : cursosPendientes) {
            log.info("** curso.codigo={} curso.id={}", nota.getCurso().getCodigo(), nota.getCurso().getId());
        }

        // C. Intentar encontrar una combinación válida (BACKTRACKING)
        Map<NotaAlumnoNivelacion, CursoNivelacion> solucion = new HashMap<>();

        buscarCombinacionParcial(0, cursosPendientes, slotsOcupados, solucion, cursosDisponiblesMap, horariosMap);

        // D. Si hubo éxito, persistir cambios
        if (!solucion.isEmpty()) {
            aplicarMatricula(alumno, solucion, ds);
        }
        return !solucion.isEmpty();
    }

    private void buscarCombinacionParcial(int indice,
                                      List<NotaAlumnoNivelacion> pendientes,
                                      Set<TimeSlot> slotsOcupados,
                                      Map<NotaAlumnoNivelacion, CursoNivelacion> solucionActual,
                                      Map<Long, List<CursoNivelacion>> cursosDisponiblesMap,
                                      Map<String, Set<TimeSlot>> horariosMap) {

        // Caso Base: Hemos asignado curso a todas las notas pendientes
        if (indice == pendientes.size()) {
            log.info("*** finalizo busqueda de horarios");
            return;
        }

        NotaAlumnoNivelacion notaActual = pendientes.get(indice);
        Long cursoId = notaActual.getCurso().getId();
        String codigo = notaActual.getCurso().getCodigo();
        log.info("*** barriendo secciones del curso {} indice {}", codigo, indice);

        // Obtener posibles secciones para esta materia
        List<CursoNivelacion> opciones = cursosDisponiblesMap.getOrDefault(cursoId, Collections.emptyList());
        log.info("**** curso.id={} opciones={}", cursoId, opciones.size());

        // Set<TimeSlot> nuevosSlotsOcupados = new HashSet<>(slotsOcupados);
        for (CursoNivelacion opcion : opciones) {
            // 1. Validar Vacantes (en tiempo real o memoria)
            if (opcion.getMatriculados() >= opcion.getVacantes()) {
                log.info("**** no hay vacantes en {}", opcion.getCodigo());
                continue;
            }

            // 2. Validar Cruce de Horarios
            Long plantillaId = opcion.getPlantilla().getId();
            Set<TimeSlot> slotsOpcion = horariosMap.get(plantillaId + "-" + cursoId);
            if (slotsOpcion != null && tieneCruce(slotsOcupados, slotsOpcion)) {
                log.info("**** hay cruce-horarios con {}", opcion.getCodigo());
                continue;
            }

            // 3. Si pasa validaciones, "Simular" inscripción
            solucionActual.put(notaActual, opcion);
            if (slotsOpcion != null) {
                slotsOcupados.addAll(slotsOpcion);
            }

            // Restar vacante temporalmente (lógica en memoria para la recursión,
            // aunque lo ideal es solo chequear y actualizar al final para no afectar otros hilos si fuera paralelo)
            opcion.setMatriculados(opcion.getMatriculados() + 1);
            opcion.setDisponibles(opcion.getDisponibles() - 1);
            log.info("**** todo bien con la sección {}", opcion.getCodigo());
            break;
        }

        // 4. Siguiente curso: Intentar con el siguiente curso
        buscarCombinacionParcial(indice + 1, pendientes, slotsOcupados, solucionActual, cursosDisponiblesMap, horariosMap);
    }
}
