package pe.edu.lamolina.amauta.controller.consejeria.informefinal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.edu.lamolina.amauta.config.DespliegueConfig;
import pe.edu.lamolina.amauta.controller.consejeria.plantutoria.PlanTutoriaService;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.amauta.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoDerivadoAtencionDAO;
import pe.edu.lamolina.amauta.dao.consejeria.CitaConsejeroAlumnoDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.InformeFinalTutoriaDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ItemInformeFinalTutoriaDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ParteInformeTutoriaDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.amauta.zelper.misc.Acumulador;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.enums.AmbienteAplicacionEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCI;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.consejeria.EstadoCitaTutorEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.tutoria.AlumnoDerivadoAtencion;
import pe.edu.lamolina.model.tutoria.CitaConsejeroAlumno;
import pe.edu.lamolina.model.tutoria.InformeFinalTutoria;
import pe.edu.lamolina.model.tutoria.ItemInformeFinalTutoria;
import pe.edu.lamolina.model.tutoria.ParteInformeTutoria;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class InformeFinalTutoriaServiceImpl implements InformeFinalTutoriaService {

    private final AlumnoConsejeroDAO alumnoConsejeroDAO;
    private final AlumnoCicloCursoDAO alumnoCicloCursoDAO;
    private final AlumnoCicloDAO alumnoCicloDAO;
    private final AlumnoDerivadoAtencionDAO alumnoDerivadoAtencionDAO;
    private final CitaConsejeroAlumnoDAO citaConsejeroAlumnoDAO;
    private final ConsejeroDAO consejeroDAO;
    private final InformeFinalTutoriaDAO informeFinalTutoriaDAO;
    private final ItemInformeFinalTutoriaDAO itemInformeFinalTutoriaDAO;
    private final MatriculaResumenDAO matriculaResumenDAO;
    private final ParteInformeTutoriaDAO parteInformeTutoriaDAO;
    private final TipoDocumentoCompaniaDAO tipoDocumentoCompaniaDAO;

    private final DespliegueConfig despliegueConfig;
    private final SerieDocumentoService serieDocumentoService;
    private final VerificadorService verificadorService;

    private final Boolean MAYOR_E_IGUAL = Boolean.TRUE;
    private final Boolean SOLO_IGUAL = Boolean.FALSE;
    private final BigDecimal NOTA_MINIMA = new BigDecimal("10.5");
    private final String ONCE = "11";
    private final List<String> CURSOS_EXTENSION = Arrays.asList("DEPORTE", "TALLER_CULTURAL");

    @Override
    public Consejero findConsejero(Consejero consejeroForm) {
        Consejero consejero = consejeroDAO.find(consejeroForm.getId());
        if (consejero == null) {
            return new Consejero();
        }
        return consejero;
    }

    @Override
    public Boolean tienePermiso(Consejero consejero, CicloAcademico ciclo, DataSessionPivot ds) {
        if (consejero == null) {
            return false;
        }
        if (consejero.getId() == null) {
            return false;
        }

        if (consejero.getCarrera() == null) {
            return false;
        }

        Persona persona = consejero.getColaborador().getPersona();
        if (ds.getPersona().equals(persona)) {
            return true;
        }

        boolean esCoordinador = verificadorService.esCoordinadorConsejeria(ds, consejero.getCarrera());
        if (esCoordinador) {
            return true;
        }
        boolean esJefeCarrera = verificadorService.esJefeCarrera(ds, consejero.getCarrera());
        if (esJefeCarrera) {
            return true;
        }

        return verificadorService.esInformaticoOERA(ds);
    }

    @Override
    public Boolean verificarConsejero(CicloAcademico ciclo, DataSessionPivot ds) {
        Consejero consejero = consejeroDAO.findByPersonaCiclo(ds.getPersona(), ciclo);
        if (consejero == null) {
            return false;
        }

        return consejero.getEstadoEnum() == EstadoEnum.ACT;
    }

    @Override
    @Transactional
    public InformeFinalTutoria findInforme(Consejero consejeroForm, CicloAcademico ciclo, DataSessionPivot ds) {
        Consejero consejero = consejeroDAO.find(consejeroForm.getId());
        if (consejero == null) {
            InformeFinalTutoria informe = new InformeFinalTutoria();
            informe.setComentarioInforme("Es tutor no existe en el sistema");
            return informe;
        }

        boolean tienePermiso = this.tienePermiso(consejero, ciclo, ds);
        if (!tienePermiso) {
            InformeFinalTutoria informe = new InformeFinalTutoria();
            informe.setComentarioInforme("No tiene permiso para ver este informe");
            return informe;
        }

        InformeFinalTutoria informeBD = informeFinalTutoriaDAO.findActivoByConsejeroCiclo(consejero, ciclo);
        if (informeBD == null) {
            boolean esConsejero = this.verificarConsejero(ciclo, ds);
            if (!esConsejero) {
                InformeFinalTutoria informe = new InformeFinalTutoria();
                informe.setComentarioInforme("El tutor aún no ha creado el informe final");
                return informe;
            }

        } else {
            List<ItemInformeFinalTutoria> itemsInforme = itemInformeFinalTutoriaDAO.allByInforme(informeBD);
            informeBD.setItemsInforme(itemsInforme);
            return informeBD;
        }

        if (informeBD == null) {
            informeBD = informeFinalTutoriaDAO.findPendienteByConsejeroCiclo(consejero, ciclo);
        }

        if (informeBD != null) {
            List<ItemInformeFinalTutoria> itemsInforme = itemInformeFinalTutoriaDAO.allByInforme(informeBD);
            informeBD.setItemsInforme(itemsInforme);
            return informeBD;
        }

        DateTime today = new DateTime();
        TipoDocumentoCompania docInformeTutor = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.INFORME_TUTOR);

        InformeFinalTutoria informeNew = new InformeFinalTutoria();
        informeNew.setConsejero(consejero);
        informeNew.setCicloAcademico(ciclo);
        informeNew.setCarrera(consejero.getCarrera());
        informeNew.setTipoDocumento(docInformeTutor);
        informeNew.setEstadoEnum(EstadoEnum.PEN);
        informeNew.setUserRegistro(ds.getUsuario());
        informeNew.setFechaRegistro(today.toDate());
        informeFinalTutoriaDAO.save(informeNew);

        List<ItemInformeFinalTutoria> itemsInforme = new ArrayList();
        List<ParteInformeTutoria> partesInforme = parteInformeTutoriaDAO.all();
        Acumulador acumulador = new Acumulador(1);

        partesInforme.stream()
                .filter(parte -> parte.getEstadoEnum() == EstadoEnum.ACT)
                .forEach(parte -> {
                    ItemInformeFinalTutoria item = new ItemInformeFinalTutoria();
                    item.setInformeFinalTutoria(informeNew);
                    item.setParteInformeTutoria(parte);
                    item.setOrden(acumulador.getValor());
                    item.setUserRegistro(ds.getUsuario());
                    item.setFechaRegistro(today.toDate());

                    itemInformeFinalTutoriaDAO.save(item);
                    itemsInforme.add(item);
                    acumulador.incrementar();
                });

        informeNew.setItemsInforme(itemsInforme);
        return informeNew;
    }

    @Override
    @Transactional
    public void calcularCantidadesInforme(InformeFinalTutoria informeForm, CicloAcademico ciclo, DataSessionPivot ds) {
        Consejero consejero = consejeroDAO.findByPersonaCiclo(ds.getPersona(), ciclo);
        boolean esConsejero = consejero != null;
        Assert.isTrue(esConsejero, "Usted no tiene permiso para calcular las cantidades del informe");

        InformeFinalTutoria informeBD = informeFinalTutoriaDAO.find(informeForm.getId());
        Assert.isNotNull(informeBD, "No existe el informe que ha seleccionado");
        Assert.isTrue(informeBD.getConsejero().equals(consejero), "Este informe corresponde a otro tutor");
        Assert.isTrue(informeBD.getCicloAcademico().equals(ciclo), "Este informe corresponde a otro ciclo académico");
        Assert.isTrue(informeBD.getEstadoEnum() == EstadoEnum.PEN, "Este informe ya no puede ser modificado");

        List<AlumnoConsejero> tutorados = alumnoConsejeroDAO.allByConsejeroCiclo(consejero, ciclo);
        Assert.isFalse(tutorados.isEmpty(), "No tiene tutorados asignados");

        List<Alumno> alumnos = tutorados.stream().map(tuto -> tuto.getAlumno()).collect(Collectors.toList());
        List<MatriculaResumen> matriculables = matriculaResumenDAO.allByAlumnosCiclo(alumnos, ciclo);

        List<EstadoMatriculaEnum> estadosMats = Arrays.asList(MAT, RCI);
        List<MatriculaResumen> matriculados = matriculables.stream()
                .filter(mtble -> estadosMats.contains(mtble.getEstadoEnum()))
                .collect(Collectors.toList());

        List<CitaConsejeroAlumno> citasConsejero = citaConsejeroAlumnoDAO.allByAlumnosCiclo(alumnos, ciclo);
        List<CitaConsejeroAlumno> citasRealizadas = citasConsejero.stream()
                .filter(cita -> cita.getEstadoEnum() == EstadoCitaTutorEnum.REALIZADA)
                .collect(Collectors.toList());

        List<Alumno> asistentes = citasRealizadas.stream()
                .map(cita -> cita.getAlumno())
                .distinct()
                .collect(Collectors.toList());

        List<AlumnoCiclo> promedios = alumnoCicloDAO.allByCicloAlumnos(ciclo, asistentes);
        List<AlumnoCicloCurso> cursados = alumnoCicloCursoDAO.allByCicloAlumnos(ciclo, asistentes);
        Map<String, AlumnoCicloCurso> mapCursados = cursados.stream()
                .collect(Collectors.toMap(cursado -> this.getKeyCursado(cursado), Function.identity()));
        boolean todosConNotas = this.todosTienenNotas(matriculados, promedios);

        Map<Long, AlumnoCiclo> mapAprobados = promedios.stream()
                .filter(prom -> prom.getPromedioCiclo().compareTo(NOTA_MINIMA) >= 0)
                .collect(Collectors.toMap(prom -> prom.getAlumno().getId(), Function.identity()));

        Map<Long, List<CitaConsejeroAlumno>> mapCitasRealizadas = citasRealizadas.stream()
                .collect(Collectors.groupingBy(cita -> cita.getAlumno().getId(), Collectors.toList()));

        List<Alumno> alumnosNoAtendidos = tutorados.stream()
                .map(tuto -> tuto.getAlumno())
                .filter(alu -> {
                    List<CitaConsejeroAlumno> citasAsistencia = mapCitasRealizadas.get(alu.getId());
                    if (citasAsistencia == null) {
                        return false;
                    }
                    return !citasAsistencia.isEmpty();
                })
                .collect(Collectors.toList());

        List<AlumnoDerivadoAtencion> derivaciones = alumnoDerivadoAtencionDAO.allByAlumnosCiclo(alumnos, ciclo);
        Acumulador acumulador = new Acumulador();

        List<ItemInformeFinalTutoria> items = itemInformeFinalTutoriaDAO.allByInforme(informeBD);
        for (ItemInformeFinalTutoria item : items) {
            // tutorados asignados
            if (item.getParteInformeTutoria().getCodigo().equals("PARTE001")) {
                this.modificarCantidad(matriculados.size(), item, acumulador, ds);
            }

            // tutorados que asistieron a citas individuales
            if (item.getParteInformeTutoria().getCodigo().equals("PARTE002")) {
                this.modificarCantidad(asistentes.size(), item, acumulador, ds);
            }

            // tutorados que asistieron a 1 cita individual
            if (item.getParteInformeTutoria().getCodigo().equals("PARTE003")) {
                List<Alumno> alumnos1Cita = this.getAlumnosPorCantidad(mapCitasRealizadas, 1, SOLO_IGUAL);
                this.modificarCantidad(alumnos1Cita.size(), item, acumulador, ds);
            }

            // tutorados que asistieron a 2 citas individuales
            if (item.getParteInformeTutoria().getCodigo().equals("PARTE004")) {
                List<Alumno> alumnos2Citas = this.getAlumnosPorCantidad(mapCitasRealizadas, 2, SOLO_IGUAL);
                this.modificarCantidad(alumnos2Citas.size(), item, acumulador, ds);
            }

            // tutorados que asistieron a 3 citas individuales
            if (item.getParteInformeTutoria().getCodigo().equals("PARTE005")) {
                List<Alumno> alumnos3Citas = this.getAlumnosPorCantidad(mapCitasRealizadas, 3, MAYOR_E_IGUAL);
                this.modificarCantidad(alumnos3Citas.size(), item, acumulador, ds);
            }

            // tutorados que no tuvieron citas individuales
            if (item.getParteInformeTutoria().getCodigo().equals("PARTE006")) {
                this.modificarCantidad(alumnosNoAtendidos.size(), item, acumulador, ds);
            }

            // atendidos con promedio aprobado
            if (todosConNotas && item.getParteInformeTutoria().getCodigo().equals("PARTE007")) {
                this.modificarCantidad(mapAprobados.size(), item, acumulador, ds);
            }

            // tutorados derivados a otros servicios
            if (item.getParteInformeTutoria().getCodigo().equals("PARTE008")) {
                List<Alumno> derivados = derivaciones.stream()
                        .map(derivacion -> derivacion.getAlumno())
                        .distinct()
                        .collect(Collectors.toList());
                this.modificarCantidad(derivados.size(), item, acumulador, ds);
            }

            // tutorados derivados a asesorias academicos
            if (item.getParteInformeTutoria().getCodigo().equals("PARTE009")) {
                List<Alumno> derivados = derivaciones.stream()
                        .filter(derivacion -> derivacion.getTipoAtencionTutorado().getCodigo().equals("ASESORIA_CURSO"))
                        .map(derivacion -> derivacion.getAlumno())
                        .distinct()
                        .collect(Collectors.toList());
                this.modificarCantidad(derivados.size(), item, acumulador, ds);
            }

            // tutorados derivados a asesorias academicos que aprobaron el ciclo
            if (todosConNotas && item.getParteInformeTutoria().getCodigo().equals("PARTE010")) {
                List<Alumno> derivados = derivaciones.stream()
                        .filter(derivacion -> derivacion.getTipoAtencionTutorado().getCodigo().equals("ASESORIA_CURSO"))
                        .filter(derivacion -> mapCursados.get(this.getKeyCursado(derivacion)) != null)
                        .filter(derivacion -> mapCursados.get(this.getKeyCursado(derivacion)).getNota().compareTo(ONCE) >= 0)
                        .map(derivacion -> derivacion.getAlumno())
                        .distinct()
                        .collect(Collectors.toList());
                this.modificarCantidad(derivados.size(), item, acumulador, ds);
            }

            // tutorados derivados a nivelacion de ingresantes
            if (item.getParteInformeTutoria().getCodigo().equals("PARTE011")) {
                List<Alumno> derivados = derivaciones.stream()
                        .filter(derivacion -> derivacion.getTipoAtencionTutorado().getCodigo().equals("NIVELACION_INGRESANTE"))
                        .map(derivacion -> derivacion.getAlumno())
                        .distinct()
                        .collect(Collectors.toList());
                this.modificarCantidad(derivados.size(), item, acumulador, ds);
            }

            // tutorados derivados a nivelacion de ingresantes que aprobaron el ciclo
            // ** NO EXISTE
            if (todosConNotas && item.getParteInformeTutoria().getCodigo().equals("PARTE012")) {
                List<Alumno> derivados = derivaciones.stream()
                        .filter(derivacion -> derivacion.getTipoAtencionTutorado().getCodigo().equals("NIVELACION_INGRESANTE"))
                        .filter(derivacion -> mapCursados.get(this.getKeyCursado(derivacion)) != null)
                        .filter(derivacion -> mapCursados.get(this.getKeyCursado(derivacion)).getNota().compareTo(ONCE) >= 0)
                        .map(derivacion -> derivacion.getAlumno())
                        .distinct()
                        .collect(Collectors.toList());
                this.modificarCantidad(derivados.size(), item, acumulador, ds);
            }

            // tutorados derivados a integración estudiantil
            // ** NO EXISTE
            if (item.getParteInformeTutoria().getCodigo().equals("PARTE013")) {
                List<Alumno> derivados = derivaciones.stream()
                        .filter(derivacion -> derivacion.getTipoAtencionTutorado().getCodigo().equals("INTEGRACION_ESTUDIANTIL"))
                        .map(derivacion -> derivacion.getAlumno())
                        .distinct()
                        .collect(Collectors.toList());
                this.modificarCantidad(derivados.size(), item, acumulador, ds);
            }

            // tutorados derivados a seminarios de reforzamiento
            if (item.getParteInformeTutoria().getCodigo().equals("PARTE014")) {
                List<Alumno> derivados = derivaciones.stream()
                        .filter(derivacion -> derivacion.getTipoAtencionTutorado().getCodigo().equals("SEMINARIO_REFORZAR"))
                        .map(derivacion -> derivacion.getAlumno())
                        .distinct()
                        .collect(Collectors.toList());
                this.modificarCantidad(derivados.size(), item, acumulador, ds);
            }

            // tutorados derivados a psicología
            if (item.getParteInformeTutoria().getCodigo().equals("PARTE015")) {
                List<Alumno> derivados = derivaciones.stream()
                        .filter(derivacion -> derivacion.getTipoAtencionTutorado().getCodigo().equals("PSICOLOGIA"))
                        .map(derivacion -> derivacion.getAlumno())
                        .distinct()
                        .collect(Collectors.toList());
                this.modificarCantidad(derivados.size(), item, acumulador, ds);
            }

            // tutorados derivados a psicopedagogía
            if (item.getParteInformeTutoria().getCodigo().equals("PARTE016")) {
                List<Alumno> derivados = derivaciones.stream()
                        .filter(derivacion -> derivacion.getTipoAtencionTutorado().getCodigo().equals("PSICOPEDAGIA"))
                        .map(derivacion -> derivacion.getAlumno())
                        .distinct()
                        .collect(Collectors.toList());
                this.modificarCantidad(derivados.size(), item, acumulador, ds);
            }

            // tutorados derivados a psicopedagogía
            if (item.getParteInformeTutoria().getCodigo().equals("PARTE017")) {
                List<Alumno> derivados = derivaciones.stream()
                        .filter(derivacion -> derivacion.getTipoAtencionTutorado().getCodigo().equals("PSICOPEDAGIA"))
                        .map(derivacion -> derivacion.getAlumno())
                        .distinct()
                        .collect(Collectors.toList());
                this.modificarCantidad(derivados.size(), item, acumulador, ds);
            }

            // tutorados derivados a programa cre-cer
            // ** NO EXISTE
            if (item.getParteInformeTutoria().getCodigo().equals("PARTE018")) {
                List<Alumno> derivados = derivaciones.stream()
                        .filter(derivacion -> derivacion.getTipoAtencionTutorado().getCodigo().equals("PROGRAMA_CRECER"))
                        .map(derivacion -> derivacion.getAlumno())
                        .distinct()
                        .collect(Collectors.toList());
                this.modificarCantidad(derivados.size(), item, acumulador, ds);
            }

            // tutorados derivados a programa impulsa
            // ** NO EXISTE
            if (item.getParteInformeTutoria().getCodigo().equals("PARTE019")) {
                List<Alumno> derivados = derivaciones.stream()
                        .filter(derivacion -> derivacion.getTipoAtencionTutorado().getCodigo().equals("PROGRAMA_IMPULSA"))
                        .map(derivacion -> derivacion.getAlumno())
                        .distinct()
                        .collect(Collectors.toList());
                this.modificarCantidad(derivados.size(), item, acumulador, ds);
            }

            // tutorados derivados a programa lideremos
            // ** NO EXISTE
            if (item.getParteInformeTutoria().getCodigo().equals("PARTE020")) {
                List<Alumno> derivados = derivaciones.stream()
                        .filter(derivacion -> derivacion.getTipoAtencionTutorado().getCodigo().equals("PROGRAMA_LIDEREMOS"))
                        .map(derivacion -> derivacion.getAlumno())
                        .distinct()
                        .collect(Collectors.toList());
                this.modificarCantidad(derivados.size(), item, acumulador, ds);
            }

            // tutorados derivados a programa lideremos
            if (item.getParteInformeTutoria().getCodigo().equals("PARTE021")) {
                List<Alumno> derivados = derivaciones.stream()
                        .filter(derivacion -> derivacion.getTipoAtencionTutorado().getCodigo().equals("TALLER_VIVENCIAL"))
                        .map(derivacion -> derivacion.getAlumno())
                        .distinct()
                        .collect(Collectors.toList());
                this.modificarCantidad(derivados.size(), item, acumulador, ds);
            }

            // tutorados derivados a cursos de extensión
            if (item.getParteInformeTutoria().getCodigo().equals("PARTE022")) {
                List<Alumno> derivados = derivaciones.stream()
                        .filter(derivacion -> CURSOS_EXTENSION.contains(derivacion.getTipoAtencionTutorado().getCodigo()))
                        .map(derivacion -> derivacion.getAlumno())
                        .distinct()
                        .collect(Collectors.toList());
                this.modificarCantidad(derivados.size(), item, acumulador, ds);
            }
        }

        Assert.isNotEqual(acumulador.getValor(), 0, "No se han encontrado cambios nuevos que cuantificar");

    }

    private void modificarCantidad(Integer cantidad, ItemInformeFinalTutoria item, Acumulador acumulador, DataSessionPivot ds) {
        log.info("[modificarCantidad] parte={} cantidad.antes={} cantidad.ahora={}", item.getParteInformeTutoria().getCodigo(), item.getCantidad(), cantidad);

        if (cantidad == null && item.getCantidad() == null) {
            return;
        }
        if (cantidad != null && item.getCantidad() != null) {
            if (Objects.equals(cantidad, item.getCantidad())) {
                return;
            }
        }
        item.setCantidad(cantidad);
        item.setUserCantidad(ds.getUsuario());
        item.setFechaCantidad(new Date());
        itemInformeFinalTutoriaDAO.update(item);

        acumulador.incrementar();
    }

    private String getKeyCursado(AlumnoCicloCurso cursado) {
        return cursado.getAlumnoCiclo().getAlumno().getId()
                + "-"
                + cursado.getCurso().getId();
    }

    private String getKeyCursado(AlumnoDerivadoAtencion derivacion) {
        return derivacion.getAlumno().getId()
                + "-"
                + derivacion.getCurso().getId();
    }

    private boolean todosTienenNotas(List<MatriculaResumen> matriculados, List<AlumnoCiclo> promedios) {
        if (matriculados.size() > promedios.size()) {
            return false;
        }

        Map<Long, AlumnoCiclo> mapPromedio = promedios.stream()
                .collect(Collectors.toMap(prom -> prom.getAlumno().getId(), Function.identity()));

        for (MatriculaResumen matriculado : matriculados) {
            Alumno alumno = matriculado.getAlumno();
            AlumnoCiclo promedio = mapPromedio.get(alumno.getId());
            if (promedio == null) {
                return false;
            }
            if (!Objects.equals(matriculado.getCursosMatriculados(), promedio.getCursosInscritos())) {
                return false;
            }
        }
        return true;
    }

    private List<Alumno> getAlumnosPorCantidad(Map<Long, List<CitaConsejeroAlumno>> mapCitas, Integer cantidad, boolean mayorEHIgual) {
        List<Alumno> alumnos = new ArrayList();

        for (List<CitaConsejeroAlumno> citas : mapCitas.values()) {
            if (mayorEHIgual && citas.size() >= cantidad) {
                alumnos.add(citas.get(0).getAlumno());
            } else if (!mayorEHIgual && citas.size() == cantidad) {
                alumnos.add(citas.get(0).getAlumno());
            }
        }

        return alumnos;
    }

    @Override
    @Transactional
    public void dificultadesInforme(InformeFinalTutoria informeForm, CicloAcademico ciclo, DataSessionPivot ds) {
        DateTime today = new DateTime();
        Consejero consejero = consejeroDAO.findByPersonaCiclo(ds.getPersona(), ciclo);
        boolean esConsejero = consejero != null;
        Assert.isTrue(esConsejero, "Usted no tiene permiso para calcular las cantidades del informe");

        InformeFinalTutoria informeBD = informeFinalTutoriaDAO.find(informeForm.getId());
        Assert.isNotNull(informeBD, "No existe el informe que ha seleccionado");
        Assert.isTrue(informeBD.getConsejero().equals(consejero), "Este informe corresponde a otro tutor");
        Assert.isTrue(informeBD.getCicloAcademico().equals(ciclo), "Este informe corresponde a otro ciclo académico");
        Assert.isTrue(informeBD.getEstadoEnum() == EstadoEnum.PEN, "Este informe ya no puede ser modificado");

        Assert.isNotNull(informeForm.getDificultades(), "No ha indicado las dificultades del informe");
        Assert.isFalse(informeForm.getDificultades().equals(informeBD.getDificultades()), "No ha enviado datos nuevos para guardar");

        informeBD.setDificultades(informeForm.getDificultades());
        informeBD.setUserModificacion(ds.getUsuario());
        informeBD.setFechaModificacion(today.toDate());
        informeFinalTutoriaDAO.update(informeBD);
    }

    @Override
    @Transactional
    public void sugerenciasInforme(InformeFinalTutoria informeForm, CicloAcademico ciclo, DataSessionPivot ds) {
        DateTime today = new DateTime();
        Consejero consejero = consejeroDAO.findByPersonaCiclo(ds.getPersona(), ciclo);
        boolean esConsejero = consejero != null;
        Assert.isTrue(esConsejero, "Usted no tiene permiso para calcular las cantidades del informe");

        InformeFinalTutoria informeBD = informeFinalTutoriaDAO.find(informeForm.getId());
        Assert.isNotNull(informeBD, "No existe el informe que ha seleccionado");
        Assert.isTrue(informeBD.getConsejero().equals(consejero), "Este informe corresponde a otro tutor");
        Assert.isTrue(informeBD.getCicloAcademico().equals(ciclo), "Este informe corresponde a otro ciclo académico");
        Assert.isTrue(informeBD.getEstadoEnum() == EstadoEnum.PEN, "Este informe ya no puede ser modificado");

        Assert.isNotNull(informeForm.getSugerencias(), "No ha indicado las sugerencias del informe");
        Assert.isFalse(informeForm.getSugerencias().equals(informeBD.getSugerencias()), "No ha enviado datos nuevos para guardar");

        informeBD.setSugerencias(informeForm.getSugerencias());
        informeBD.setUserModificacion(ds.getUsuario());
        informeBD.setFechaModificacion(today.toDate());
        informeFinalTutoriaDAO.update(informeBD);
    }

    @Override
    @Transactional
    public void conclusionesInforme(InformeFinalTutoria informeForm, CicloAcademico ciclo, DataSessionPivot ds) {
        DateTime today = new DateTime();
        Consejero consejero = consejeroDAO.findByPersonaCiclo(ds.getPersona(), ciclo);
        boolean esConsejero = consejero != null;
        Assert.isTrue(esConsejero, "Usted no tiene permiso para calcular las cantidades del informe");

        InformeFinalTutoria informeBD = informeFinalTutoriaDAO.find(informeForm.getId());
        Assert.isNotNull(informeBD, "No existe el informe que ha seleccionado");
        Assert.isTrue(informeBD.getConsejero().equals(consejero), "Este informe corresponde a otro tutor");
        Assert.isTrue(informeBD.getCicloAcademico().equals(ciclo), "Este informe corresponde a otro ciclo académico");
        Assert.isTrue(informeBD.getEstadoEnum() == EstadoEnum.PEN, "Este informe ya no puede ser modificado");

        Assert.isNotNull(informeForm.getConclusiones(), "No ha indicado las conclusiones del informe");
        Assert.isFalse(informeForm.getConclusiones().equals(informeBD.getConclusiones()), "No ha enviado datos nuevos para guardar");

        informeBD.setConclusiones(informeForm.getConclusiones());
        informeBD.setUserModificacion(ds.getUsuario());
        informeBD.setFechaModificacion(today.toDate());
        informeFinalTutoriaDAO.update(informeBD);
    }

    @Override
    @Transactional
    public void enviarInforme(InformeFinalTutoria informeForm, CicloAcademico ciclo, DataSessionPivot ds) {
        DateTime today = new DateTime();
        Consejero consejero = consejeroDAO.findByPersonaCiclo(ds.getPersona(), ciclo);
        boolean esConsejero = consejero != null;
        Assert.isTrue(esConsejero, "Usted no tiene permiso para calcular las cantidades del informe");

        InformeFinalTutoria informeBD = informeFinalTutoriaDAO.find(informeForm.getId());
        Assert.isNotNull(informeBD, "No existe el informe que ha seleccionado");
        Assert.isTrue(informeBD.getConsejero().equals(consejero), "Este informe corresponde a otro tutor");
        Assert.isTrue(informeBD.getCicloAcademico().equals(ciclo), "Este informe corresponde a otro ciclo académico");
        Assert.isTrue(informeBD.getEstadoEnum() == EstadoEnum.PEN, "Este informe ya no puede ser enviado");

        Assert.isNotNull(informeBD.getDificultades(), "Falta que registre las dificultades en el informe");
        Assert.isNotNull(informeBD.getSugerencias(), "Falta que registre las sugerencias en el informe");
        Assert.isNotNull(informeBD.getConclusiones(), "Falta que registre las conclusiones en el informe");

        AmbienteAplicacionEnum ambiente = AmbienteAplicacionEnum.valueOf(despliegueConfig.getAmbiente().toUpperCase());
        if (ambiente == AmbienteAplicacionEnum.PROD) {

            List<AlumnoConsejero> tutorados = alumnoConsejeroDAO.allByConsejeroCiclo(consejero, ciclo);
            List<Alumno> alumnos = tutorados.stream().map(tuto -> tuto.getAlumno()).collect(Collectors.toList());
            List<MatriculaResumen> matriculables = matriculaResumenDAO.allByAlumnosCiclo(alumnos, ciclo);

            List<EstadoMatriculaEnum> estadosMats = Arrays.asList(MAT, RCI);
            List<MatriculaResumen> matriculados = matriculables.stream()
                    .filter(mtble -> estadosMats.contains(mtble.getEstadoEnum()))
                    .collect(Collectors.toList());

            List<CitaConsejeroAlumno> citasConsejero = citaConsejeroAlumnoDAO.allByAlumnosCiclo(alumnos, ciclo);
            List<CitaConsejeroAlumno> citasRealizadas = citasConsejero.stream()
                    .filter(cita -> cita.getEstadoEnum() == EstadoCitaTutorEnum.REALIZADA)
                    .collect(Collectors.toList());

            List<Alumno> asistentes = citasRealizadas.stream()
                    .map(cita -> cita.getAlumno())
                    .distinct()
                    .collect(Collectors.toList());

            List<AlumnoCiclo> promedios = alumnoCicloDAO.allByCicloAlumnos(ciclo, asistentes);
            boolean todosConNotas = this.todosTienenNotas(matriculados, promedios);
            Assert.isTrue(todosConNotas, "Los alumnos aún no tienen sus notas completas");

            List<ItemInformeFinalTutoria> items = itemInformeFinalTutoriaDAO.allByInforme(informeBD);
            for (ItemInformeFinalTutoria item : items) {
                Assert.isNotNull(item.getCantidad(), "No ha calculado las cantidades de <b>" + item.getParteInformeTutoria().getNombre() + "</b>");
            }
        }

        TipoDocumentoCompania tipoDocumento = informeBD.getTipoDocumento();
        Long serieInforme = Integer.valueOf(today.getYear()).longValue();
        SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumento, serieInforme, ds.getUsuario());
        String numero = NumberFormat.codigo(Integer.valueOf(serieDocumento.getNumeroDocumento()), 3);

        informeBD.setEstadoEnum(EstadoEnum.ACT);
        informeBD.setSerie(serieDocumento.getNumeroSerie());
        informeBD.setNumero(numero);
        informeBD.setFecha(today.toDate());
        informeBD.setUserEmision(ds.getUsuario());
        informeBD.setFechaEmision(today.toDate());
        informeFinalTutoriaDAO.update(informeBD);
    }

}
