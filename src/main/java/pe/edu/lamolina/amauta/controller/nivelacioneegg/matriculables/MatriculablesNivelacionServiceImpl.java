package pe.edu.lamolina.amauta.controller.nivelacioneegg.matriculables;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.matriculables.dto.BuscarCruceDTO;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.matriculables.dto.MatriculablesResumen;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.CursoCicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioCursoDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.AlumnoNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.NotaAlumnoNivelacionDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoTemaExamenDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.calificacion.TemaExamen;

import static pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum.CER;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;

import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.PlantillaNivelacion;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioCurso;
import pe.edu.lamolina.model.nivelacioneegg.AlumnoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.CursoTemaExamen;
import pe.edu.lamolina.amauta.dao.horario.PlantillaNivelacionDAO;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class MatriculablesNivelacionServiceImpl implements MatriculablesNivelacionService {

    private final AlumnoNivelacionDAO alumnoNivelacionDAO;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final CursoCicloAcademicoDAO cursoCicloAcademicoDAO;
    private final CursoNivelacionDAO cursoNivelacionDAO;
    private final CursoTemaExamenDAO cursoTemaExamenDAO;
    private final PlantillaNivelacionDAO plantillaNivelacionDAO;
    private final HorarioCursoDAO horarioCursoDAO;
    private final NotaAlumnoNivelacionDAO notaAlumnoNivelacionDAO;

    private final VerificadorService verificadorService;

    private void verificarPermiso(DataSessionPivot ds) {
        boolean esOperador = verificadorService.esOperadorEEGG(ds);
        Assert.isTrue(esOperador, "No tiene permiso para ejecutar esta operación");
    }

    @Override
    public CicloAcademico findCiclo(CicloAcademico ciclo) {
        return cicloAcademicoDAO.findByCiclo(ciclo);
    }

    @Override
    public List<PlantillaNivelacion> allPlantillas() {
        return plantillaNivelacionDAO.all();
    }

    @Override
    public List<NotaAlumnoNivelacion> allMatriculablesByDynatable(DynatableFilter filter, CicloAcademico ciclo) {
        List<NotaAlumnoNivelacion> matriculables = notaAlumnoNivelacionDAO.allByDynatableCiclo(filter, ciclo);

        return matriculables;
    }

    @Override
    public MatriculablesResumen resumen(CicloAcademico ciclo, DataSessionPivot ds) {
        MatriculablesResumen resumen = notaAlumnoNivelacionDAO.findResumen(ciclo);
        if (resumen.getInscritos() == null) {
            resumen.setInscritos(0L);
        }
        if (resumen.getPendientes() == null) {
            resumen.setPendientes(0L);
        }
        return resumen;
    }

    @Override
    @Transactional
    public int generarMatriculables(CicloAcademico ciclo, DataSessionPivot ds) {
        this.verificarPermiso(ds);

        List<NotaAlumnoNivelacion> matbles = notaAlumnoNivelacionDAO.allActivosByCiclo(ciclo);
        Map<String, NotaAlumnoNivelacion> mapMatbles = new HashMap();
        for (NotaAlumnoNivelacion matble : matbles) {
            String key = matble.getAlumnoNivelacion().getAlumno().getId() + "-";
            key += matble.getCurso().getId();
            mapMatbles.put(key, matble);
        }

        List<NotaAlumnoNivelacion> nuevosMtbles = notaAlumnoNivelacionDAO.allSinCursoByCiclo(ciclo);
        if (nuevosMtbles.isEmpty()) {
            return nuevosMtbles.size();
        }

        List<CursoNivelacion> cursosNiv = cursoNivelacionDAO.allActivosByCiclo(ciclo);
        if (cursosNiv.isEmpty()) {
            return cursosNiv.size();
        }

        Map<Long, List<CursoNivelacion>> mapCursoNiv = cursosNiv.stream()
                .collect(Collectors.groupingBy(cn -> cn.getCursoCiclo().getCurso().getId()));

        List<CursoTemaExamen> cursosTemas = cursoTemaExamenDAO.all();
        Map<Long, List<CursoTemaExamen>> mapCursoTema = cursosTemas.stream()
                .collect(Collectors.groupingBy(cte -> cte.getTemaExamen().getId()));

        int nuevos = 0;
        for (NotaAlumnoNivelacion mtble : nuevosMtbles) {
            Alumno alumno = mtble.getAlumnoNivelacion().getAlumno();
            if (mtble.getTemaAprobado()) {
                continue;
            }

            TemaExamen tema = mtble.getTemaExamen();
            List<CursoTemaExamen> cursosTema = mapCursoTema.get(tema.getId());
            if (cursosTema == null) {
                continue;
            }

            for (CursoTemaExamen cursoTema : cursosTema) {
                Curso curso = cursoTema.getCurso();
                String key = alumno.getId() + "-" + curso.getId();
                NotaAlumnoNivelacion existe = mapMatbles.get(key);
                if (existe != null) {
                    mtble.setEsMatriculable(Boolean.FALSE);
                    mtble.setCurso(curso);
                    notaAlumnoNivelacionDAO.update(mtble);
                    continue;
                }

                List<CursoNivelacion> cursosMtbles = mapCursoNiv.get(curso.getId());
                if (cursosMtbles == null) {
                    continue;
                }

                mtble.setEsMatriculable(Boolean.TRUE);
                mtble.setCurso(curso);
                notaAlumnoNivelacionDAO.update(mtble);
                mapMatbles.put(key, mtble);
                nuevos++;
            }
        }

        return nuevos;
    }

    @Override
    @Transactional
    public int matriculaMasivaTipo1(CicloAcademico ciclo, DataSessionPivot ds) {
        this.verificarPermiso(ds);

        List<NotaAlumnoNivelacion> nuevosMtbles = notaAlumnoNivelacionDAO.allConCursoByCiclo(ciclo);
        if (nuevosMtbles.isEmpty()) {
            return nuevosMtbles.size();
        }

        List<NotaAlumnoNivelacion> matriculados = notaAlumnoNivelacionDAO.allMatriculadosByCiclo(ciclo);
        Map<Long, List<PlantillaNivelacion>> mapAlumnoPlantilla = matriculados.stream()
                .collect(Collectors.groupingBy(
                        mat -> mat.getAlumnoNivelacion().getId(),
                        Collectors.mapping(
                                mat -> mat.getCursoNivelacion().getPlantilla(),
                                Collectors.collectingAndThen(Collectors.toSet(), ArrayList::new)
                        )
                ));

        List<CursoNivelacion> cursosNiv = cursoNivelacionDAO.allActivosByCiclo(ciclo);

        Map<Long, List<CursoNivelacion>> mapCursoNiv = cursosNiv.stream()
                .collect(Collectors.groupingBy(cn -> cn.getCursoCiclo().getCurso().getId()));

        int nuevos = 0;
        List<NotaAlumnoNivelacion> faltantes = new ArrayList();
        for (NotaAlumnoNivelacion mtble : nuevosMtbles) {
            if (mtble.getTemaAprobado()) {
                continue;
            }

            Curso curso = mtble.getCurso();
            List<CursoNivelacion> cursosMtbles = mapCursoNiv.get(curso.getId());
            if (cursosMtbles == null) {
                continue;
            }

            AlumnoNivelacion alumnoNiv = mtble.getAlumnoNivelacion();
            List<PlantillaNivelacion> plantilla = mapAlumnoPlantilla.get(alumnoNiv.getId());
            boolean procesar = plantilla == null ? true : plantilla.size() == 1;
            if (!procesar) {
                faltantes.add(mtble);
                continue;
            }

            PlantillaNivelacion gpoAlumno = null;
            if (plantilla != null) {
                gpoAlumno = plantilla.get(0);
            }

            boolean registrado = false;
            for (CursoNivelacion cursoNiv : cursosMtbles) {
                PlantillaNivelacion plantillaSeccion = cursoNiv.getPlantilla();
                boolean registrar = plantilla == null || (gpoAlumno != null && plantillaSeccion.equals(gpoAlumno));

                if (cursoNiv.getDisponibles() > 0 && registrar) {
                    cursoNiv.setDisponibles(cursoNiv.getDisponibles() - 1);
                    cursoNiv.setMatriculados(cursoNiv.getMatriculados() + 1);
                    cursoNivelacionDAO.update(cursoNiv);

                    mtble.setCursoNivelacion(cursoNiv);
                    mtble.setEstadoEnum(MAT);
                    mtble.setUserModificacion(ds.getUsuario());
                    mtble.setFechaModificacion(new Date());
                    notaAlumnoNivelacionDAO.update(mtble);
                    registrado = true;
                    nuevos++;

                    if (alumnoNiv.getEstadoEnum() != MAT) {
                        alumnoNiv.setEstadoEnum(MAT);
                        alumnoNiv.setFechaModificacion(new Date());
                        alumnoNiv.setUserModificacion(ds.getUsuario());
                        alumnoNivelacionDAO.update(alumnoNiv);
                    }

                    if (plantilla == null) {
                        plantilla = new ArrayList();
                        plantilla.add(plantillaSeccion);
                        mapAlumnoPlantilla.put(alumnoNiv.getId(), plantilla);
                    }

                    break;
                }
            }

            if (!registrado) {
                faltantes.add(mtble);
            }
        }

        Map<Long, List<HorarioCurso>> mapHorario = new HashMap();

        for (NotaAlumnoNivelacion mtble : faltantes) {
            if (mtble.getTemaAprobado()) {
                continue;
            }

            Curso curso = mtble.getCurso();
            List<CursoNivelacion> cursosMtbles = mapCursoNiv.get(curso.getId());
            if (cursosMtbles == null) {
                continue;
            }

            AlumnoNivelacion alumnoNiv = mtble.getAlumnoNivelacion();
            List<HorarioCurso> horariosExistentes = this.getHorariosCurso(mapHorario, alumnoNiv);

            for (CursoNivelacion cursoNiv : cursosMtbles) {
                if (cursoNiv.getDisponibles() > 0) {
                    PlantillaNivelacion plantilla = cursoNiv.getPlantilla();
                    CursoCicloAcademico cursoCiclo = cursoNiv.getCursoCiclo();
                    List<HorarioCurso> horariosNuevos = horarioCursoDAO.allByCursoCicloPlantilla(cursoCiclo, plantilla);

                    boolean hayCruce = this.buscandoCruces(horariosExistentes, horariosNuevos);
                    if (hayCruce) {
                        continue;
                    }

                    cursoNiv.setDisponibles(cursoNiv.getDisponibles() - 1);
                    cursoNiv.setMatriculados(cursoNiv.getMatriculados() + 1);
                    cursoNivelacionDAO.update(cursoNiv);

                    mtble.setCursoNivelacion(cursoNiv);
                    mtble.setEstadoEnum(MAT);
                    mtble.setUserModificacion(ds.getUsuario());
                    mtble.setFechaModificacion(new Date());
                    notaAlumnoNivelacionDAO.update(mtble);
                    nuevos++;

                    if (alumnoNiv.getEstadoEnum() != MAT) {
                        alumnoNiv.setEstadoEnum(MAT);
                        alumnoNiv.setFechaModificacion(new Date());
                        alumnoNiv.setUserModificacion(ds.getUsuario());
                        alumnoNivelacionDAO.update(alumnoNiv);
                    }

                    break;
                }
            }

        }

        return nuevos;
    }

    private boolean buscandoCruces(List<HorarioCurso> horariosOtros, List<HorarioCurso> horariosNuevos) {

        if (horariosOtros.isEmpty()) {
            horariosOtros.addAll(horariosNuevos);
            return false;
        }

        if (horariosNuevos.isEmpty()) {
            return false;
        }

        Map<String, HorarioCurso> mapHorarioCurso = horariosNuevos.stream()
                .collect(Collectors.toMap(hor -> hor.getKey(), Function.identity()));

        for (HorarioCurso hor : horariosOtros) {
            String key = hor.getKey();
            HorarioCurso hc = mapHorarioCurso.get(key);
            if (hc != null) {
                return true;
            }
        }

        horariosOtros.addAll(horariosNuevos);
        return false;
    }

    private List<HorarioCurso> getHorariosCurso(Map<Long, List<HorarioCurso>> mapHorario, AlumnoNivelacion alumnoNiv) {
        List<HorarioCurso> horarios = mapHorario.get(alumnoNiv.getId());
        if (horarios != null) {
            return horarios;
        }

        List<HorarioCurso> horariosOtros = new ArrayList();
        mapHorario.put(alumnoNiv.getId(), horariosOtros);

        List<NotaAlumnoNivelacion> notasAll = notaAlumnoNivelacionDAO.allByAlumnoNivelacion(alumnoNiv);
        if (notasAll.isEmpty()) {
            return horariosOtros;
        }

        List<NotaAlumnoNivelacion> notas = notasAll.stream()
                .filter(nan -> nan.getEstadoEnum() == MAT)
                .filter(nan -> nan.getAlumnoNivelacion().getEstadoEnum() == MAT)
                .filter(nan -> nan.getEsMatriculable())
                .filter(nan -> nan.getCursoNivelacion() != null)
                .collect(Collectors.toList());
        if (notas.isEmpty()) {
            return horariosOtros;
        }

        for (NotaAlumnoNivelacion nota : notas) {
            CursoCicloAcademico cursoCiclo = nota.getCursoNivelacion().getCursoCiclo();
            PlantillaNivelacion plantilla = nota.getCursoNivelacion().getPlantilla();
            List<HorarioCurso> horariosCurso = horarioCursoDAO.allByCursoCicloPlantilla(cursoCiclo, plantilla);

            horariosOtros.addAll(horariosCurso);
        }

        return horariosOtros;
    }

    @Override
    public NotaAlumnoNivelacion infoAlumno(NotaAlumnoNivelacion form, CicloAcademico ciclo, DataSessionPivot ds) {
        NotaAlumnoNivelacion info = notaAlumnoNivelacionDAO.find(form.getId());
        Assert.isNotNull(info, "No existe el registro que ha seleccionado");
        Assert.isNull(info.getCursoNivelacion(), "Ya se encuetra matriculado");
        Assert.isTrue(info.getEstadoEnum() == NMAT, "No se encuetra habilitado en este registro");

        CicloAcademico cicloBD = info.getAlumnoNivelacion().getCicloAcademico();
        Assert.isTrue(cicloBD.getId().equals(ciclo.getId()), "El registro no corresponde el ciclo actual");

        CursoNivelacion cursoNiv = new CursoNivelacion();

        List<NotaAlumnoNivelacion> notas = notaAlumnoNivelacionDAO.allByAlumnoNivelacion(info.getAlumnoNivelacion());
        List<PlantillaNivelacion> plantillas = notas.stream()
                .filter(nan -> nan.getEstadoEnum() == MAT)
                .filter(nan -> nan.getEsMatriculable())
                .filter(nan -> nan.getCursoNivelacion() != null)
                .map(nan -> nan.getCursoNivelacion().getPlantilla())
                .distinct()
                .collect(Collectors.toList());
        if (plantillas.size() == 1) {
            cursoNiv.setPlantilla(plantillas.get(0));
        }

        info.setCursoNivelacion(cursoNiv);
        return info;
    }

    @Override
    public String verificarCruce(BuscarCruceDTO form, CicloAcademico ciclo, DataSessionPivot ds) {
        Assert.isNotNull(form.getPlantilla(), "No ha indicado la plantilla");
        Assert.isNotNull(form.getPlantilla().getId(), "No ha indicado la plantilla");

        PlantillaNivelacion plantillaBD = plantillaNivelacionDAO.find(form.getPlantilla().getId());
        Assert.isNotNull(plantillaBD, "La plantilla que ha seleccionado no existe en el sistema");

        Assert.isNotNull(form.getCursoCiclo(), "No ha indicado el curso");
        Assert.isNotNull(form.getCursoCiclo().getCurso(), "No ha indicado el curso");
        Assert.isNotNull(form.getCursoCiclo().getCurso().getId(), "No ha indicado el curso");

        CursoCicloAcademico cursoCiclo = cursoCicloAcademicoDAO.findByCursoCiclo(form.getCursoCiclo().getCurso(), ciclo);
        Assert.isNotNull(cursoCiclo, "Este curso programado no está programado en este ciclo");

        Assert.isNotNull(form.getAlumnoNivelacion(), "No ha indicado el alumno");
        Assert.isNotNull(form.getAlumnoNivelacion().getId(), "No ha indicado el alumno");

        AlumnoNivelacion alumnoNiv = alumnoNivelacionDAO.find(form.getAlumnoNivelacion().getId());
        Assert.isNotNull(alumnoNiv, "El alumno que ha seleccionado no existe en el sistema");
        CicloAcademico cicloNiv = alumnoNiv.getCicloAcademico();
        Assert.isTrue(cicloNiv.getId().equals(ciclo.getId()), "El ciclo del alumno no corresponde al ciclo actual");

        List<HorarioCurso> horariosNuevos = horarioCursoDAO.allByCursoCicloPlantilla(cursoCiclo, plantillaBD);
        if (horariosNuevos.isEmpty()) {
            return null;
        }

        List<NotaAlumnoNivelacion> notasAll = notaAlumnoNivelacionDAO.allByAlumnoNivelacion(alumnoNiv);
        if (notasAll.isEmpty()) {
            return null;
        }

        List<NotaAlumnoNivelacion> notas = notasAll.stream()
                .filter(nan -> nan.getEstadoEnum() == MAT)
                .filter(nan -> nan.getAlumnoNivelacion().getEstadoEnum() == MAT)
                .filter(nan -> nan.getEsMatriculable())
                .filter(nan -> nan.getCursoNivelacion() != null)
                .collect(Collectors.toList());
        if (notas.isEmpty()) {
            return null;
        }

        List<HorarioCurso> horariosOtros = new ArrayList();
        for (NotaAlumnoNivelacion nota : notas) {
            CursoCicloAcademico otroCursoCiclo = nota.getCursoNivelacion().getCursoCiclo();
            PlantillaNivelacion otraPlantilla = nota.getCursoNivelacion().getPlantilla();
            List<HorarioCurso> horariosCurso = horarioCursoDAO.allByCursoCicloPlantilla(otroCursoCiclo, otraPlantilla);

            horariosOtros.addAll(horariosCurso);
        }

        Map<String, HorarioCurso> mapHorarioCurso = horariosNuevos.stream()
                .collect(Collectors.toMap(hor -> hor.getKey(), Function.identity()));

        for (HorarioCurso hor : horariosOtros) {
            Dia dia = hor.getDia();
            Hora hora = hor.getHora();
            String key = hor.getKey();
            String fecha = TypesUtil.getStringDate(hor.getSemana(), "dd 'de' MMM", "es");

            HorarioCurso hc = mapHorarioCurso.get(key);
            if (hc != null) {
                return "Hay cruce de horario el " + dia.getSimboloAbr() + " a las " + hora.getDescripcion() + " de la semana del " + fecha;
            }
        }

        return null;
    }

    @Override
    public List<CursoNivelacion> allSecciones(CursoNivelacion form, CicloAcademico ciclo, DataSessionPivot ds) {
        Assert.isNotNull(form.getPlantilla(), "No ha indicado la plantilla");
        Assert.isNotNull(form.getPlantilla().getId(), "No ha indicado la plantilla");

        Assert.isNotNull(form.getCursoCiclo(), "No ha indicado el curso");
        Assert.isNotNull(form.getCursoCiclo().getCurso(), "No ha indicado el curso");
        Assert.isNotNull(form.getCursoCiclo().getCurso().getId(), "No ha indicado el curso");

        CursoCicloAcademico cursoCiclo = cursoCicloAcademicoDAO.findByCursoCiclo(form.getCursoCiclo().getCurso(), ciclo);
        Assert.isNotNull(cursoCiclo, "Este curso programado no está programado en este ciclo");

        return cursoNivelacionDAO.allByCursoCicloPlantilla(cursoCiclo, form.getPlantilla()).stream()
                .filter(cn -> cn.getEstadoEnum() == SeccionEstadoEnum.ACT)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public synchronized void matricularCurso(NotaAlumnoNivelacion form, CicloAcademico ciclo, DataSessionPivot ds) {
        this.verificarPermiso(ds);

        Assert.isNotNull(form.getCursoNivelacion(), "No ha indicado la sección al cual matricularse");
        Assert.isNotNull(form.getCursoNivelacion().getId(), "No ha indicado la sección al cual matricularse");

        NotaAlumnoNivelacion mtble = notaAlumnoNivelacionDAO.find(form.getId());
        Assert.isNotNull(mtble, "No existe el registro que ha seleccionado");
        Assert.isNull(mtble.getCursoNivelacion(), "Ya se encuetra matriculado");
        Assert.isTrue(mtble.getEstadoEnum() == NMAT, "No se encuetra habilitado en este registro");

        CicloAcademico cicloBD = mtble.getAlumnoNivelacion().getCicloAcademico();
        Assert.isTrue(cicloBD.getId().equals(ciclo.getId()), "El registro no corresponde el ciclo actual");
        Assert.isTrue(!mtble.getTemaAprobado(), "Este tema ya aprobó");
        Assert.isTrue(mtble.getEsMatriculable(), "Este registro no es matriculable");
        Assert.isNotNull(mtble.getCurso(), "Este registro debe estar relacionado a algún curso");

        CursoNivelacion cursoNiv = cursoNivelacionDAO.find(form.getCursoNivelacion().getId());
        Assert.isNotNull(cursoNiv, "No existe la sección que ha seleccionado");
        Assert.isTrue(cursoNiv.getEstadoEnum() == SeccionEstadoEnum.ACT, "Esta sección no está habilitada para inscribirse");
        Assert.isTrue(cursoNiv.getDisponibles() > 0, "Ya no existe vacantes disponibles");
        Assert.isFalse(cursoNiv.getEstadoNotasEnum() == CER, "Esta sección ya cerró su acta de notas");

        cursoNiv.setDisponibles(cursoNiv.getDisponibles() - 1);
        cursoNiv.setMatriculados(cursoNiv.getMatriculados() + 1);
        cursoNivelacionDAO.update(cursoNiv);

        mtble.setCursoNivelacion(cursoNiv);
        mtble.setEstadoEnum(MAT);
        mtble.setUserModificacion(ds.getUsuario());
        mtble.setFechaModificacion(new Date());
        notaAlumnoNivelacionDAO.update(mtble);

        AlumnoNivelacion alumnoNiv = mtble.getAlumnoNivelacion();
        if (alumnoNiv.getEstadoEnum() != MAT) {
            alumnoNiv.setEstadoEnum(MAT);
            alumnoNiv.setFechaModificacion(new Date());
            alumnoNiv.setUserModificacion(ds.getUsuario());
            alumnoNivelacionDAO.update(alumnoNiv);
        }
    }

    @Override
    @Transactional
    public synchronized void retirarCurso(NotaAlumnoNivelacion form, CursoNivelacion seccion, CicloAcademico ciclo, DataSessionPivot ds) {
        this.verificarPermiso(ds);

        Assert.isNotNull(form.getCursoNivelacion(), "No ha indicado la sección de la cual retirar");
        Assert.isNotNull(form.getCursoNivelacion().getId(), "No ha indicado la sección de la cual retirar");

        NotaAlumnoNivelacion mtble = notaAlumnoNivelacionDAO.find(form.getId());
        Assert.isNotNull(mtble, "No existe el registro que ha seleccionado");
        Assert.isNotNull(mtble.getCursoNivelacion(), "Ya se encuetra matriculado");
        Assert.isFalse(mtble.getEstadoEnum() == NMAT, "No se encuetra inscrito en este registro");

        CicloAcademico cicloBD = mtble.getAlumnoNivelacion().getCicloAcademico();
        Assert.isTrue(cicloBD.getId().equals(ciclo.getId()), "El registro no corresponde el ciclo actual");

        CursoNivelacion seccionBD = seccion == null ? null : seccion;
        if (seccion == null) {
            seccionBD = cursoNivelacionDAO.find(form.getCursoNivelacion().getId());
        }

        Assert.isNotNull(seccionBD, "No existe la sección que ha seleccionado");
        Assert.isTrue(seccionBD.getId().equals(form.getCursoNivelacion().getId()), "La sección no corresponde al registro seleccionado");
        Assert.isFalse(seccionBD.getEstadoNotasEnum() == CER, "Esta sección ya cerró su acta de notas");

        seccionBD.setDisponibles(seccionBD.getDisponibles() + 1);
        seccionBD.setMatriculados(seccionBD.getMatriculados() - 1);
        cursoNivelacionDAO.update(seccionBD);

        mtble.setCursoNivelacion(null);
        mtble.setEstadoEnum(NMAT);
        mtble.setUserModificacion(ds.getUsuario());
        mtble.setFechaModificacion(new Date());
        notaAlumnoNivelacionDAO.update(mtble);

        AlumnoNivelacion alumnoNiv = mtble.getAlumnoNivelacion();
        List<NotaAlumnoNivelacion> notasAll = notaAlumnoNivelacionDAO.allByAlumnoNivelacion(alumnoNiv);
        List<NotaAlumnoNivelacion> matriculados = notasAll.stream()
                .filter(nan -> nan.getEstadoEnum() == MAT)
                .collect(Collectors.toList());
        log.info("[retirarCurso] alumnoNiv.id={} matriculados={}", alumnoNiv.getId(), matriculados.size());

        if (matriculados.isEmpty() && alumnoNiv.getEstadoEnum() != NMAT) {
            alumnoNiv.setEstadoEnum(NMAT);
            alumnoNiv.setFechaModificacion(new Date());
            alumnoNiv.setUserModificacion(ds.getUsuario());
            alumnoNivelacionDAO.update(alumnoNiv);
        }
    }

}
