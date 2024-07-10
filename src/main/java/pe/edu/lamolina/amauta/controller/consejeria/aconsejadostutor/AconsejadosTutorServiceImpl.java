package pe.edu.lamolina.amauta.controller.consejeria.aconsejadostutor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.consejeria.aconsejadostutor.view.ResumenEncuestaTutoria;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.bean.AconsejadoEstadoBean;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoCualidadDAO;
import pe.edu.lamolina.amauta.dao.consejeria.CitaConsejeroAlumnoDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.InformeFinalTutoriaDAO;
import pe.edu.lamolina.amauta.dao.consejeria.PlanTutorialDAO;
import pe.edu.lamolina.amauta.dao.encuesta.EncuestaPublicadaDAO;
import pe.edu.lamolina.amauta.dao.encuesta.OpcionPreguntaDAO;
import pe.edu.lamolina.amauta.dao.encuesta.PreguntaExamenDAO;
import pe.edu.lamolina.amauta.dao.encuesta.RespuestaEncuestaDAO;
import pe.edu.lamolina.amauta.dao.encuesta.TipoExamenVirtualDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.consejeria.Consejero;
import static pe.edu.lamolina.model.constantines.GlobalConstantine.ID_CONSEJERO_NN;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.examen.EncuestaPublicada;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.OpcionPregunta;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.examen.RespuestaEncuesta;
import pe.edu.lamolina.model.examen.TipoExamenVirtual;
import pe.edu.lamolina.model.tutoria.AlumnoCualidad;
import pe.edu.lamolina.model.tutoria.CitaConsejeroAlumno;
import pe.edu.lamolina.model.tutoria.InformeFinalTutoria;
import pe.edu.lamolina.model.tutoria.PlanTutorial;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class AconsejadosTutorServiceImpl implements AconsejadosTutorService {

    private final AlumnoCualidadDAO alumnoCualidadDAO;
    private final AlumnoConsejeroDAO alumnoConsejeroDAO;
    private final CitaConsejeroAlumnoDAO citaConsejeroAlumnoDAO;
    private final ConsejeroDAO consejeroDAO;
    private final EncuestaPublicadaDAO encuestaPublicadaDAO;
    private final InformeFinalTutoriaDAO informeFinalTutoriaDAO;
    private final MatriculaResumenDAO matriculaResumenDAO;
    private final OpcionPreguntaDAO opcionPreguntaDAO;
    private final PersonaDAO personaDAO;
    private final PlanTutorialDAO planTutorialDAO;
    private final PreguntaExamenDAO preguntaExamenDAO;
    private final RespuestaEncuestaDAO respuestaEncuestaDAO;
    private final TipoExamenVirtualDAO tipoExamenVirtualDAO;

    private final VerificadorService verificadorService;
    private final BigDecimal CIEN = new BigDecimal(100);

    @Override
    public Consejero findConsejero(Persona persona, CicloAcademico ciclo) {
        Consejero consejero = consejeroDAO.findByPersonaCiclo(persona, ciclo);
        if (consejero == null) {
            return new Consejero();
        }
        return consejero;
    }

    @Override
    public List<Consejero> allConsejeroCarrera(Persona persona, CicloAcademico cicloAcademico) {
        List<Consejero> consejeros = consejeroDAO.allByPersonaCiclo(persona, cicloAcademico);
        if (consejeros.isEmpty()) {
            return new ArrayList<>();
        }
        return consejeros;
    }

    @Override
    public InformeFinalTutoria findInforme(Consejero consejero, CicloAcademico ciclo, DataSessionPivot ds) {
        if (consejero == null) {
            return new InformeFinalTutoria();
        }
        if (consejero.getId() == null) {
            return new InformeFinalTutoria();
        }
        if (consejero.getColaborador().getPersona().equals(ds.getPersona())) {
            InformeFinalTutoria informe = informeFinalTutoriaDAO.findByConsejeroCiclo(consejero, ciclo);
            if (informe != null) {
                return informe;
            }
        }
        return new InformeFinalTutoria();
    }

    @Override
    public List<AlumnoConsejero> allByDynatable(DynatableFilter filter, CicloAcademico ciclo, Persona tutor) {
        List<AlumnoConsejero> alumnoConsejeros = alumnoConsejeroDAO.allByDynatablePersonaTutor(filter, ciclo, tutor);
        List<Alumno> alumnos = alumnoConsejeros.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<MatriculaResumen> matriculaResumen = matriculaResumenDAO.allByAlumnosCiclo(alumnos, ciclo);
        Map<Long, MatriculaResumen> mapMatriculaResumen = TypesUtil.convertListToMap("alumno.id", matriculaResumen);

        for (AlumnoConsejero alumnoTutor : alumnoConsejeros) {
            MatriculaResumen matResumen = mapMatriculaResumen.get(alumnoTutor.getAlumno().getId());
            if (matResumen != null) {
                alumnoTutor.setEstadoMatriculableEnum(matResumen.getEstadoEnum());
                alumnoTutor.setEstadoMatriculaAutorizacion(matResumen.getAutorizacionMatricula());
                alumnoTutor.setCursosMatriculados(matResumen.getCursosMatriculados());
                alumnoTutor.setCreditosMatriculados(matResumen.getCreditosMatriculados());
            } else {
                alumnoTutor.setEstadoMatriculableEnum(EstadoMatriculaEnum.INH);
            }
        }
        return alumnoConsejeros;
    }

    @Override
    public List<AlumnoConsejero> allByCicloPersona(CicloAcademico cicloAcademico, Persona persona) {
        return alumnoConsejeroDAO.allByCicloPersona(cicloAcademico, persona);
    }

    @Override
    public List<AlumnoConsejero> allByDynatableByCarrera(DynatableFilter filter, CicloAcademico ciclo, Persona tutor, Carrera carrera, DataSessionPivot ds) {

//        List<AlumnoConsejero> alumnoConsejeros = null;
//        if (verificadorService.isDeveloperOERA(ds)) {
//            alumnoConsejeros = alumnoConsejeroDAO.allByDynatablePersonaTutorCarreraOERA(filter, ciclo, tutor, carrera);
//        } else {
//         alumnoConsejeros = alumnoConsejeroDAO.allByDynatablePersonaTutorCarrera(filter, ciclo, tutor, carrera);
//        }
        List<AlumnoConsejero> alumnoConsejeros = alumnoConsejeroDAO.allByDynatablePersonaTutorCarreraOERA(filter, ciclo, tutor, carrera);

        List<Alumno> alumnos = alumnoConsejeros.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<MatriculaResumen> matriculaResumen = matriculaResumenDAO.allByAlumnosCiclo(alumnos, ciclo);

        Map<Long, MatriculaResumen> mapMatriculaResumen = TypesUtil.convertListToMap("alumno.id", matriculaResumen);

        for (AlumnoConsejero alumnoTutor : alumnoConsejeros) {
            MatriculaResumen matResumen = mapMatriculaResumen.get(alumnoTutor.getAlumno().getId());
            if (matResumen != null) {
                alumnoTutor.setEstadoMatriculableEnum(matResumen.getEstadoEnum());
                alumnoTutor.setEstadoMatriculaAutorizacion(matResumen.getAutorizacionMatricula());
                alumnoTutor.setCursosMatriculados(matResumen.getCursosMatriculados());
                alumnoTutor.setCreditosMatriculados(matResumen.getCreditosMatriculados());
            } else {
                alumnoTutor.setEstadoMatriculableEnum(EstadoMatriculaEnum.INH);
            }
        }

        return alumnoConsejeros;

    }

    @Override
    public List<AlumnoConsejero> allByDynatableByCarreraReporte(DynatableFilter filter, CicloAcademico ciclo, Persona tutor, Carrera carrera) {

        List<AlumnoConsejero> alumnoConsejeros = alumnoConsejeroDAO.allByDynatablePersonaTutorCarrera(filter, ciclo, tutor, carrera);

        List<Alumno> alumnos = alumnoConsejeros.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<MatriculaResumen> matriculaResumen = matriculaResumenDAO.allByAlumnosCiclo(alumnos, ciclo);

        Map<Long, MatriculaResumen> mapMatriculaResumen = TypesUtil.convertListToMap("alumno.id", matriculaResumen);

        for (AlumnoConsejero alumnoTutor : alumnoConsejeros) {
            MatriculaResumen matResumen = mapMatriculaResumen.get(alumnoTutor.getAlumno().getId());
            if (matResumen != null) {
                alumnoTutor.setEstadoMatriculableEnum(matResumen.getEstadoEnum());
                alumnoTutor.setEstadoMatriculaAutorizacion(matResumen.getAutorizacionMatricula());
                alumnoTutor.setCursosMatriculados(matResumen.getCursosMatriculados());
                alumnoTutor.setCreditosMatriculados(matResumen.getCreditosMatriculados());
            } else {
                alumnoTutor.setEstadoMatriculableEnum(EstadoMatriculaEnum.INH);
            }
        }

        return alumnoConsejeros;

    }

    @Override
    public AconsejadoEstadoBean allByPersona(Persona persona, CicloAcademico ciclo) {
        Long countMatriculable = matriculaResumenDAO.countMatriculablesByConsejero(persona, ciclo);
        Long countNoMatriculados = matriculaResumenDAO.countNoMatriculablesByConsejero(persona, ciclo);
        Long countRetiroCiclo = matriculaResumenDAO.countRetiroCicloByConsejero(persona, ciclo);
        AconsejadoEstadoBean aconsejadoEstadoBean = new AconsejadoEstadoBean();
        aconsejadoEstadoBean.setMatriculados(countMatriculable);
        aconsejadoEstadoBean.setNoMatriculados(countNoMatriculados);
        aconsejadoEstadoBean.setRetiroCiclo(countRetiroCiclo);
        return aconsejadoEstadoBean;
    }

    @Override
    @Transactional
    public void matriculaAutorizacion(MatriculaResumen matriculaResumen, DataSessionPivot ds) {
        MatriculaResumen matriculaResumenBD = matriculaResumenDAO.findByAlumnoCiclo(matriculaResumen.getAlumno(), ds.getCicloAcademico());
        matriculaResumenBD.setAutorizacionMatricula(matriculaResumen.getAutorizacionMatricula());
        matriculaResumenBD.setFechaAutorizacionMatricula(new Date());
        matriculaResumenBD.setUserConsejero(ds.getUsuario());
        matriculaResumenDAO.update(matriculaResumenBD);
    }

    @Override
    public Persona findPersona(Long idPersona) {
        return personaDAO.find(idPersona);
    }

    @Override
    public AconsejadoEstadoBean allByPersonaCarrera(Persona persona, CicloAcademico ciclo, Carrera carrera, DataSessionPivot ds) {
        DynatableFilter filter = new DynatableFilter();
        filter.setPage(1);
        filter.setOffset(0);
        filter.setPerPage(10000000);

        Long countMatriculable = matriculaResumenDAO.countMatriculablesByConsejeroCarrera(persona, ciclo, carrera);
        Long countNoMatriculados = matriculaResumenDAO.countNoMatriculablesByConsejeroCarrera(persona, ciclo, carrera);
        Long countRetiroCiclo = matriculaResumenDAO.countRetiroCicloByConsejeroCarrera(persona, ciclo, carrera);
        Long countNoMatriculable = matriculaResumenDAO.countNoMatriculableByConsejeroCarrera(persona, ciclo, carrera);

//        List<AlumnoConsejero> alumnosTutor = null;
//        if (verificadorService.isDeveloperOERA(ds)) {
//            alumnosTutor = alumnoConsejeroDAO.allByDynatablePersonaTutorCarreraOERA(filter, ciclo, persona, carrera);
//        } else {
//            alumnosTutor = alumnoConsejeroDAO.allByDynatablePersonaTutorCarrera(filter, ciclo, persona, carrera);
//        }
        List<AlumnoConsejero> alumnosTutor = alumnoConsejeroDAO.allByDynatablePersonaTutorCarreraOERA(filter, ciclo, persona, carrera);

        AconsejadoEstadoBean aconsejadoEstadoBean = new AconsejadoEstadoBean();
        aconsejadoEstadoBean.setMatriculados(countMatriculable);
        aconsejadoEstadoBean.setNoMatriculados(countNoMatriculados);
        aconsejadoEstadoBean.setRetiroCiclo(countRetiroCiclo);
        aconsejadoEstadoBean.setAlumnosConsejeros(alumnosTutor);
        aconsejadoEstadoBean.setNoMatriculables(countNoMatriculable);
        return aconsejadoEstadoBean;
    }

    @Override
    @Transactional
    public void eliminarAlumnoConsejero(Long idAlumnoConsejero) {
        alumnoConsejeroDAO.delete(idAlumnoConsejero);
    }

    @Override
    @Transactional
    public void quitarTutor(Long idAlumnoConsejero) {
        AlumnoConsejero alumnoConsejero = alumnoConsejeroDAO.findAll(idAlumnoConsejero);
        alumnoConsejero.setConsejero(new Consejero(ID_CONSEJERO_NN));
        alumnoConsejeroDAO.updateColumns(alumnoConsejero, "consejero");
    }

    @Override
    public Map<Long, List<PlanTutorial>> allPlanes(List<Alumno> alumnos, CicloAcademico ciclo) {
        List<PlanTutorial> planesAll = planTutorialDAO.allByAlumnosCiclo(alumnos, ciclo);
        Map<Long, List<PlanTutorial>> mapPlanes = new HashMap();

        for (Alumno alumno : alumnos) {
            List<PlanTutorial> planes = planesAll.stream().filter(plan -> plan.getAlumno().equals(alumno)).collect(Collectors.toList());
            mapPlanes.put(alumno.getId(), planes);
        }

        return mapPlanes;
    }

    @Override
    public Map<Long, List<AlumnoCualidad>> allCualidades(List<Alumno> alumnos, CicloAcademico ciclo) {
        List<AlumnoCualidad> cualidadesAll = alumnoCualidadDAO.allByAlumnos(alumnos);
        Map<Long, List<AlumnoCualidad>> mapCualidades = new HashMap();

        for (Alumno alumno : alumnos) {
            List<AlumnoCualidad> cualidades = cualidadesAll.stream().filter(plan -> plan.getAlumno().equals(alumno)).collect(Collectors.toList());
            mapCualidades.put(alumno.getId(), cualidades);
        }

        return mapCualidades;
    }

    @Override
    public Map<Long, CitaConsejeroAlumno> allCitas(List<Alumno> alumnos, CicloAcademico ciclo) {
        List<CitaConsejeroAlumno> citasAll = citaConsejeroAlumnoDAO.allUltimosByAlumnosCiclo(alumnos, ciclo);
        Map<Long, CitaConsejeroAlumno> mapCitas = new HashMap();

        for (Alumno alumno : alumnos) {
            CitaConsejeroAlumno cita = citasAll.stream().filter(citaConse -> citaConse.getAlumno().equals(alumno)).findFirst().orElse(null);
            if (cita == null) {
                cita = new CitaConsejeroAlumno();
            }
            mapCitas.put(alumno.getId(), cita);
        }

        return mapCitas;
    }

    @Override
    public List<PreguntaExamen> allPreguntasEncuesta(CicloAcademico ciclo) {
        TipoExamenVirtual tipoEncuTutor = tipoExamenVirtualDAO.findByEnum(TipoExamenVirtualEnum.ENC_TUTOR);
        List<EncuestaPublicada> publicaciones = encuestaPublicadaDAO.allByCicloTipo(ciclo, tipoEncuTutor);

        if (publicaciones.isEmpty()) {
            return new ArrayList();
        }

        ExamenVirtual encuesta = publicaciones.get(0).getExamenVirtual();
        List<PreguntaExamen> preguntas = preguntaExamenDAO.allActivasByEncuesta(encuesta);

        List<OpcionPregunta> opcionesAll = opcionPreguntaDAO.allByPreguntas(preguntas);
        Map<Long, List<OpcionPregunta>> mapOpciones = opcionesAll.stream()
                .collect(Collectors.groupingBy(opcion -> opcion.getPregunta().getId(), Collectors.toList()));

        preguntas.forEach(pgta -> {
            List<OpcionPregunta> opciones = mapOpciones.get(pgta.getId());
            pgta.setOpcionesPregunta(opciones);
        });

        return preguntas;
    }

    @Override
    public List<ResumenEncuestaTutoria> allDataEncuesta(Consejero consejero, List<PreguntaExamen> preguntas, CicloAcademico ciclo, DataSessionPivot ds) {
        if (preguntas.isEmpty()) {
            return new ArrayList();
        }

        List<AlumnoConsejero> tutorados = alumnoConsejeroDAO.allByConsejeroCiclo(consejero, ciclo);
        List<Alumno> alumnos = tutorados.stream()
                .map(tuto -> tuto.getAlumno())
                .collect(Collectors.toList());

        if (alumnos.isEmpty()) {
            return new ArrayList();
        }

        ExamenVirtual encuesta = preguntas.get(0).getExamenVirtual();
        List<OpcionPregunta> opciones = preguntas.get(0).getOpcionesPregunta();
        List<RespuestaEncuesta> respuestas = respuestaEncuestaDAO.allByAlumnosEncuestaCiclo(alumnos, encuesta, ciclo);

        Date fechaMax = respuestas.stream()
                .map(rpta -> rpta.getAlumnoEncuesta().getFechaFin())
                .max(Comparator.naturalOrder())
                .orElse(null);

        Date fechaMin = respuestas.stream()
                .map(rpta -> rpta.getAlumnoEncuesta().getFechaFin())
                .min(Comparator.naturalOrder())
                .orElse(null);

        List<Alumno> encuestados = respuestas.stream()
                .map(rpta -> rpta.getAlumnoEncuesta().getAlumno())
                .distinct()
                .collect(Collectors.toList());

        List<ResumenEncuestaTutoria> resumenes = new ArrayList();
        for (PreguntaExamen pregunta : preguntas) {
            ResumenEncuestaTutoria resumen = new ResumenEncuestaTutoria();

            Map<String, BigDecimal> puntajes = new HashMap();
            for (OpcionPregunta opcion : opciones) {
                BigDecimal porcentaje = this.getPorcentaje(pregunta, opcion.getLetra(), respuestas, encuestados);
                puntajes.put(opcion.getLetra(), porcentaje);
            }

            resumen.setDesde(fechaMin);
            resumen.setHasta(fechaMax);
            resumen.setEncuestados(encuestados.size());
            resumen.setPregunta(pregunta);
            resumen.setPuntajes(puntajes);
            resumenes.add(resumen);
        }

        return resumenes;
    }

    private BigDecimal getPorcentaje(PreguntaExamen pregunta, String letra, List<RespuestaEncuesta> respuestas, List<Alumno> encuestados) {
        if (encuestados.isEmpty()) {
            return BigDecimal.ZERO;
        }

        List<RespuestaEncuesta> seleccionadas = respuestas.stream()
                .filter(rpta -> rpta.getOpcionRespuesta().getPregunta().getId().equals(pregunta.getId()))
                .filter(rpta -> rpta.getOpcionRespuesta().getLetra().equals(letra))
                .collect(Collectors.toList());

        if (seleccionadas.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalEncuestados = new BigDecimal(encuestados.size());
        BigDecimal respondones = new BigDecimal(seleccionadas.size());
        BigDecimal porcentaje = respondones.multiply(CIEN).divide(totalEncuestados, 4, RoundingMode.HALF_UP);

        return porcentaje;
    }

}
