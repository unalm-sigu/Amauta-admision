package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.docente;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.encuestaestudiantil.ConfiguraEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.CursoSinEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaAlumno;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocenteModalidad;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.encuestaestudiantil.PeriodoEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocenteModalidad;
import pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.TemaExamenVirtual;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.encuesta.ConfiguraEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.CursoSinEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaAlumnoDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaDocenteDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaDocenteModalidadDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaEstudiantilDAO;
import pe.edu.lamolina.pivot.dao.encuesta.PeriodoEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.PuntajeEncuestaDocenteModalidadDAO;
import pe.edu.lamolina.pivot.dao.encuesta.TemaExamenVirtualDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class GeneradorEncuestaDocenteServiceImp implements GeneradorEncuestaDocenteService {
    
    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;
    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;
    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;
    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;
    @Autowired
    EncuestaEstudiantilDAO encuestaEstudiantilDAO;
    @Autowired
    ConfiguraEncuestaDAO configuraEncuestaDAO;
    @Autowired
    CursoSinEncuestaDAO cursoSinEncuestaDAO;
    @Autowired
    EncuestaDocenteDAO encuestaDocenteDAO;
    @Autowired
    EncuestaAlumnoDAO encuestaAlumnoDAO;
    @Autowired
    PeriodoEncuestaDAO periodoEncuestaDAO;
    @Autowired
    EncuestaDocenteModalidadDAO encuestaDocenteModalidadDAO;
    @Autowired
    PuntajeEncuestaDocenteModalidadDAO puntajeEncuestaDocenteModalidadDAO;
    @Autowired
    TemaExamenVirtualDAO temaExamenVirtualDAO;
    
    @Autowired
    VisorEncuestaDocente visorEncuestaDocente;
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Async
    @Override
    @Transactional
    public void generarEncuesta(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        visorEncuestaDocente.setEstado("Obteniendo información de matriculados");
        List<MatriculaSeccion> matriculasSecciones = matriculaSeccionDAO.allMatriculadosByCiclo(cicloAcademico);
        Map<Long, List<Alumno>> mapAlumnos = TypesUtil.convertListToMapList("seccion.id", "matriculaResumen.alumno", matriculasSecciones);
        for (Map.Entry<Long, List<Alumno>> entry : mapAlumnos.entrySet()) {
            List<Alumno> alumnos = clearAlumnosDuplicados(entry.getValue());
            mapAlumnos.put(entry.getKey(), alumnos);
        }
        
        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        visorEncuestaDocente.setEstado("Obteniendo información de docentes-secciones");
        List<DocenteSeccion> profesPersonasSecciones = docenteSeccionDAO.allSinNNByCicloModalidad(cicloAcademico, modalidad);
        List<DocenteSeccion> profesActivosSecciones = docenteSeccionDAO.allActivosByCiclo(cicloAcademico);
        List<EncuestaDocenteModalidad> encusProfesModalidadades = encuestaDocenteModalidadDAO.allByCiclo(cicloAcademico);
        Map<Long, List<DocenteSeccion>> mapProfeSeccBySecc = TypesUtil.convertListToMapList("seccion.id", profesActivosSecciones);
        Map<Long, List<DocenteSeccion>> mapProfeSeccByGpoSecc = TypesUtil.convertListToMapList("seccion.grupoSeccion.id", profesActivosSecciones);
        Map<String, EncuestaDocenteModalidad> mapEncusProfesModalidadades = TypesUtil.convertListToMap("key", encusProfesModalidadades);
        
        EncuestaEstudiantil encuestaEstudiantil = encuestaEstudiantilDAO.findByCicloTipo(cicloAcademico, TipoExamenVirtualEnum.ENC_DOC);
        ConfiguraEncuesta configuraEncuesta = configuraEncuestaDAO.findByEncuesta(encuestaEstudiantil);
        List<PeriodoEncuesta> periodosEncuesta = periodoEncuestaDAO.allByEncuesta(encuestaEstudiantil);
        List<TemaExamenVirtual> temas = temaExamenVirtualDAO.allByEvaluacion(encuestaEstudiantil.getEncuesta());
        encuestaEstudiantil.getEncuesta().setTema(temas);
        
        List<EncuestaDocente> encuestasDocentes = encuestaDocenteDAO.allByEncuestaEstudiantil(encuestaEstudiantil);
        Map<Long, EncuestaDocente> mapEncuestaByProfeSecc = TypesUtil.convertListToMap("docenteSeccion.id", encuestasDocentes);
        
        List<CursoSinEncuesta> cursosNoEncuestar = cursoSinEncuestaDAO.allByEncuestaEstudiantil(encuestaEstudiantil);
        Map<Long, Curso> mapCursosNoEncuestar = TypesUtil.convertListToMap("curso.di", "curso", cursosNoEncuestar);
        
        visorEncuestaDocente.iniciarConteo(profesPersonasSecciones.size());
        for (DocenteSeccion profeSecc : profesPersonasSecciones) {
            saveEncuestaDocente(
                    profeSecc,
                    mapAlumnos,
                    mapProfeSeccBySecc,
                    mapProfeSeccByGpoSecc,
                    mapEncuestaByProfeSecc,
                    mapCursosNoEncuestar,
                    mapEncusProfesModalidadades,
                    configuraEncuesta,
                    periodosEncuesta,
                    encuestaEstudiantil, ds);
            visorEncuestaDocente.incrementar();
        }

        //encuestaEstudiantil.setUserModificacion(ds.getUsuario());
        //encuestaEstudiantil.setFechaModificacion(new Date());
        encuestaEstudiantilDAO.update(encuestaEstudiantil);
        
    }
    
    private void saveEncuestaDocente(
            DocenteSeccion profeSecc,
            Map<Long, List<Alumno>> mapAlumnos,
            Map<Long, List<DocenteSeccion>> mapProfeSeccBySeccion,
            Map<Long, List<DocenteSeccion>> mapProfeSeccByGpoSeccion,
            Map<Long, EncuestaDocente> mapEncuestaByProfeSecc,
            Map<Long, Curso> mapCursosNoEncuestar,
            Map<String, EncuestaDocenteModalidad> mapEncusProfesModalidadades,
            ConfiguraEncuesta configuraEncuesta,
            List<PeriodoEncuesta> periodosEncuesta,
            EncuestaEstudiantil encuestaEstudiantil,
            DataSessionPivot ds) {
        
        EncuestaDocente encuProfe = mapEncuestaByProfeSecc.get(profeSecc.getId());
        if (encuProfe != null) {
            return;
        }
        
        Seccion seccion = profeSecc.getSeccion();
        List<DocenteSeccion> profesoresSecc = mapProfeSeccBySeccion.get(seccion.getId());
        List<Alumno> alumnos = mapAlumnos.get(seccion.getId());
        alumnos = (alumnos == null) ? new ArrayList() : alumnos;
        
        Docente docente = profeSecc.getDocente();
        Curso curso = seccion.getGrupoSeccion().getCurso();
        ModalidadEstudio modalidad = curso.getModalidadEstudio();
        CicloAcademico ciclo = profeSecc.getSeccion().getGrupoSeccion().getCicloAcademico();
        
        EncuestaDocenteModalidad encuProfeModalidad = mapEncusProfesModalidadades.get(docente.getId() + "-" + modalidad.getId());
        if (encuProfeModalidad == null) {
            encuProfeModalidad = new EncuestaDocenteModalidad();
            encuProfeModalidad.setCicloAcademico(ciclo);
            encuProfeModalidad.setModalidadEstudio(modalidad);
            encuProfeModalidad.setDocente(docente);
            encuProfeModalidad.setAlumnosEncuestados(0);
            encuProfeModalidad.setEstadoEnum(EncuestaEstudiantilEstadoEnum.INA);
            encuProfeModalidad.setPuntajeBase5(BigDecimal.ZERO);
            encuProfeModalidad.setPuntajeBase10(BigDecimal.ZERO);
            encuestaDocenteModalidadDAO.save(encuProfeModalidad);
            
            mapEncusProfesModalidadades.put(encuProfeModalidad.getKey(), encuProfeModalidad);
            
            ExamenVirtual encuesta = encuestaEstudiantil.getEncuesta();
            List<TemaExamenVirtual> temas = encuesta.getTema();
            for (TemaExamenVirtual tema : temas) {
                PuntajeEncuestaDocenteModalidad puntaje = new PuntajeEncuestaDocenteModalidad();
                puntaje.setEncuestaDocenteModalidad(encuProfeModalidad);
                puntaje.setTemaEncuesta(tema);
                puntaje.setPuntaje(BigDecimal.ZERO);
                puntaje.setDesvisacionStandar(BigDecimal.ZERO);
                puntajeEncuestaDocenteModalidadDAO.save(puntaje);
            }
        }
        
        String impedido = null;
        if (profesoresSecc.size() > configuraEncuesta.getCantidadMaximaDocentes()) {
            impedido = "Anulada porque excede la cantidad máxima de docentes. ";
        }
        
        if (alumnos.size() < configuraEncuesta.getCantidadMinimaAlumnos()) {
            impedido = (impedido == null ? "" : impedido);
            impedido += "Anulada porque no tiene la cantidad mínima de alumnos. ";
        }
        
        Curso cursoNoEnc = mapCursosNoEncuestar.get(curso.getId());
        if (cursoNoEnc != null) {
            impedido = (impedido == null ? "" : impedido);
            impedido += "Anulada porque este curso está configurado para no ser encuestado. ";
        }
        
        if (impedido != null) {
            EncuestaDocente enc = new EncuestaDocente();
            enc.setAlumnosEncuestados(0L);
            enc.setAlumnosInicio(Long.valueOf(alumnos.size()));
            enc.setAlumnosFin(Long.valueOf(alumnos.size()));
            enc.setDescripcion(impedido);
            enc.setDocenteSeccion(profeSecc);
            enc.setEncuestaEstudiantil(encuestaEstudiantil);
            enc.setModalidadEstudio(curso.getModalidadEstudio());
            enc.setEsTeoriaPractica(0);
            enc.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ANU);
            enc.setUserRegistro(ds.getUsuario());
            enc.setFechaRegistro(new Date());
            encuestaDocenteDAO.save(enc);
            return;
        }
        
        if (profesoresSecc.size() == 1 && configuraEncuesta.getEncuestaTeoriaPractica() == 0l) {
            for (PeriodoEncuesta periodo : periodosEncuesta) {
                EncuestaDocente enc = new EncuestaDocente();
                enc.setAlumnosEncuestados(0L);
                enc.setAlumnosInicio(Long.valueOf(alumnos.size()));
                enc.setAlumnosFin(Long.valueOf(alumnos.size()));
                enc.setDescripcion(impedido);
                enc.setDocenteSeccion(profeSecc);
                enc.setEncuestaEstudiantil(encuestaEstudiantil);
                enc.setEsTeoriaPractica(configuraEncuesta.getEncuestaTeoriaPractica().intValue());
                enc.setModalidadEstudio(curso.getModalidadEstudio());
                enc.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
                enc.setFechaEncuestaInicio(periodo.getFechaInicio());
                enc.setFechaEncuestaFin(periodo.getFechaFin());
                enc.setUserRegistro(ds.getUsuario());
                enc.setFechaRegistro(new Date());
                encuestaDocenteDAO.save(enc);
                
                saveEncuestaAlumno(encuProfe, alumnos, ds);
                encuestaEstudiantil.setObjetivosEncuesta(encuestaEstudiantil.getObjetivosEncuesta() + 1);
                encuestaEstudiantil.setEncuestasProgramadas(encuestaEstudiantil.getEncuestasProgramadas() + alumnos.size());
                
                encuProfeModalidad.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
                encuestaDocenteModalidadDAO.update(encuProfeModalidad);
            }
            return;
        }
        
        if (configuraEncuesta.getEncuestaTeoriaPractica() == 1l) {
            if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {
                GrupoSeccion gpoSecc = profeSecc.getSeccion().getGrupoSeccion();
                List<DocenteSeccion> profeSeccByGpoSecc = mapProfeSeccByGpoSeccion.get(gpoSecc.getId());
                boolean esProfeTeoriaPractica = esProfeTeoriaPractica(docente, profeSeccByGpoSecc);
                if (esProfeTeoriaPractica) {
                    EncuestaDocente enc = new EncuestaDocente();
                    enc.setAlumnosEncuestados(0L);
                    enc.setAlumnosInicio(Long.valueOf(alumnos.size()));
                    enc.setAlumnosFin(Long.valueOf(alumnos.size()));
                    enc.setDocenteSeccion(profeSecc);
                    enc.setEncuestaEstudiantil(encuestaEstudiantil);
                    enc.setModalidadEstudio(curso.getModalidadEstudio());
                    enc.setEsTeoriaPractica(0);
                    enc.setEstadoEnum(EncuestaEstudiantilEstadoEnum.TEO);
                    enc.setDescripcion("Se encuesta en la teoría");
                    enc.setUserRegistro(ds.getUsuario());
                    enc.setFechaRegistro(new Date());
                    encuestaDocenteDAO.save(enc);
                    return;
                }
            }
        }
        
        if (profesoresSecc.size() == 1) {
            for (PeriodoEncuesta periodo : periodosEncuesta) {
                EncuestaDocente enc = new EncuestaDocente();
                enc.setAlumnosEncuestados(0L);
                enc.setAlumnosInicio(Long.valueOf(alumnos.size()));
                enc.setAlumnosFin(Long.valueOf(alumnos.size()));
                enc.setDescripcion(impedido);
                enc.setDocenteSeccion(profeSecc);
                enc.setEncuestaEstudiantil(encuestaEstudiantil);
                enc.setEsTeoriaPractica(configuraEncuesta.getEncuestaTeoriaPractica().intValue());
                enc.setModalidadEstudio(curso.getModalidadEstudio());
                enc.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
                enc.setFechaEncuestaInicio(periodo.getFechaInicio());
                enc.setFechaEncuestaFin(periodo.getFechaFin());
                enc.setUserRegistro(ds.getUsuario());
                enc.setFechaRegistro(new Date());
                encuestaDocenteDAO.save(enc);
                
                saveEncuestaAlumno(enc, alumnos, ds);
                encuestaEstudiantil.setObjetivosEncuesta(encuestaEstudiantil.getObjetivosEncuesta() + 1);
                encuestaEstudiantil.setEncuestasProgramadas(encuestaEstudiantil.getEncuestasProgramadas() + alumnos.size());
                
                encuProfeModalidad.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
                encuestaDocenteModalidadDAO.update(encuProfeModalidad);
            }
            return;
        }
        
        if (profeSecc.getFechaFin() == null) {
            EncuestaDocente enc = new EncuestaDocente();
            enc.setAlumnosEncuestados(0L);
            enc.setAlumnosInicio(Long.valueOf(alumnos.size()));
            enc.setAlumnosFin(Long.valueOf(alumnos.size()));
            enc.setDescripcion(impedido);
            enc.setDocenteSeccion(profeSecc);
            enc.setEncuestaEstudiantil(encuestaEstudiantil);
            enc.setModalidadEstudio(curso.getModalidadEstudio());
            enc.setEsTeoriaPractica(0);
            enc.setEstadoEnum(EncuestaEstudiantilEstadoEnum.FECH);
            enc.setDescripcion("No se encuesta porque el docente no tiene configurado su periodo de clases");
            enc.setUserRegistro(ds.getUsuario());
            enc.setFechaRegistro(new Date());
            encuestaDocenteDAO.save(enc);
            return;
        }
        
        Date inicioEncuesta = new DateTime(profeSecc.getFechaFin()).minusDays(configuraEncuesta.getDiasEncuesta().intValue()).toDate();
        EncuestaDocente enc = new EncuestaDocente();
        enc.setAlumnosEncuestados(0L);
        enc.setAlumnosInicio(Long.valueOf(alumnos.size()));
        enc.setAlumnosFin(Long.valueOf(alumnos.size()));
        enc.setDescripcion(impedido);
        enc.setDocenteSeccion(profeSecc);
        enc.setEncuestaEstudiantil(encuestaEstudiantil);
        enc.setEsTeoriaPractica(configuraEncuesta.getEncuestaTeoriaPractica().intValue());
        enc.setModalidadEstudio(curso.getModalidadEstudio());
        enc.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
        enc.setFechaEncuestaInicio(inicioEncuesta);
        enc.setFechaEncuestaFin(profeSecc.getFechaFin());
        enc.setUserRegistro(ds.getUsuario());
        enc.setFechaRegistro(new Date());
        encuestaDocenteDAO.save(enc);
        
        saveEncuestaAlumno(enc, alumnos, ds);
        encuestaEstudiantil.setObjetivosEncuesta(encuestaEstudiantil.getObjetivosEncuesta() + 1);
        encuestaEstudiantil.setEncuestasProgramadas(encuestaEstudiantil.getEncuestasProgramadas() + alumnos.size());
        
        encuProfeModalidad.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
        encuestaDocenteModalidadDAO.update(encuProfeModalidad);
    }
    
    private void saveEncuestaAlumno(EncuestaDocente encuestaDocente, List<Alumno> alumnos, DataSessionPivot ds) {
        for (Alumno alumno : alumnos) {
            EncuestaAlumno encuAlumno = new EncuestaAlumno();
            encuAlumno.setAlumno(alumno);
            encuAlumno.setEncuestaDocente(encuestaDocente);
            encuAlumno.setEstadoEnum(EncuestaEstudiantilEstadoEnum.PEND);
            encuAlumno.setUserRegistro(ds.getUsuario());
            encuAlumno.setFechaRegistro(new Date());
            encuestaAlumnoDAO.save(encuAlumno);
        }
    }
    
    private boolean esProfeTeoriaPractica(Docente docente, List<DocenteSeccion> profesSecciones) {
        for (DocenteSeccion profeSecc : profesSecciones) {
            Seccion seccion = profeSecc.getSeccion();
            if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.TCUR) {
                Docente profe = profeSecc.getDocente();
                if (profe.getId().longValue() == docente.getId()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private List<Alumno> clearAlumnosDuplicados(List<Alumno> alumnosDobles) {
        Map<Long, Alumno> mapAlumnos = TypesUtil.convertListToMap("id", alumnosDobles);
        return new ArrayList(mapAlumnos.values());
    }

//    @Async
//    @Override
//    @Transactional
//    public void generarEncuesta2(CicloAcademico cicloAcademico, DataSessionPivot ds) {
//        long DAYS_IN_MS = 1000 * 60 * 60 * 24;
//
//        List<EventoAcademicoEnum> eventos = Arrays.asList(EventoAcademicoEnum.CLASES_PRE1, EventoAcademicoEnum.CLASES_PRE2);
//        List<EventoCicloAcademico> eventosCicloAcademico = eventoCicloAcademicoDAO.allActivosByCicloEventos(cicloAcademico, eventos);
//
//        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
//        List<MatriculaSeccion> matriculaSeccions = matriculaSeccionDAO.allMatriculadosByCiclo(cicloAcademico);
//        Map<Long, List<MatriculaSeccion>> mapMatriSeccBySeccion = TypesUtil.convertListToMapList("seccion.id", matriculaSeccions);
//        logger.debug("matriculaSeccions {}", matriculaSeccions.size());
//
//        List<DocenteSeccion> profesSecciones = docenteSeccionDAO.allSinNNByCicloModalidad(cicloAcademico, modalidad);
//        Map<Long, List<DocenteSeccion>> mapProfeSeccBySeccion = TypesUtil.convertListToMapList("seccion.id", profesSecciones);
//        Map<Long, List<DocenteSeccion>> mapProfeSeccByGpoSecc = TypesUtil.convertListToMapList("seccion.grupoSeccion.id", profesSecciones);
//        logger.debug("docenteSeccions {}", profesSecciones.size());
//
//        EncuestaEstudiantil encuestaEstudiantil = encuestaEstudiantilDAO.findByCicloTipo(cicloAcademico, TipoExamenVirtualEnum.ENC_DOC);
//        ConfiguraEncuesta configuraEncuesta = configuraEncuestaDAO.findByEncuesta(encuestaEstudiantil);
//
//        Long maximoDocentes = configuraEncuesta.getCantidadMaximaDocentes();
//        Long minimoAlumnos = configuraEncuesta.getCantidadMinimaAlumnos();
//        logger.debug("cantidadMaximaDocentes {} cantidadMinimaAlumnos {}", maximoDocentes, minimoAlumnos);
//
//        List<CursoSinEncuesta> cursosSinEncuesta = cursoSinEncuestaDAO.allByEncuestaEstudiantil(encuestaEstudiantil);
//        Map<Long, Curso> mapCursoNoEncuestar = TypesUtil.convertListToMap("curso.id", "curso", cursosSinEncuesta);
//
//        List<EncuestaDocente> encuestaDocentes = encuestaDocenteDAO.allByEncuestaEstudiantil(encuestaEstudiantil);
//        Map<Long, EncuestaDocente> mapEncuestaByProfeSecc = TypesUtil.convertListToMap("docenteSeccion.id", encuestaDocentes);
//        Map<Long, Map<Long, Seccion>> mapMapSeccByGpoSecc = new LinkedHashMap();
//
//        for (DocenteSeccion profeSecc : profesSecciones) {
//            Seccion seccion = profeSecc.getSeccion();
//            GrupoSeccion gpoSecc = seccion.getGrupoSeccion();
//            Map<Long, Seccion> mapSeccion = mapMapSeccByGpoSecc.get(gpoSecc.getId());
//            if (mapSeccion == null) {
//                mapSeccion = new ListHashMap();
//            }
//            mapSeccion.put(seccion.getId(), seccion);
//            mapMapSeccByGpoSecc.put(gpoSecc.getId(), mapSeccion);
//        }
//
//        Date fechaInicio = null;
//        Date fechaFin = null;
//        for (EventoCicloAcademico eventoCicloAcademico : eventosCicloAcademico) {
//            Date fechaInicioEvento = eventoCicloAcademico.getFechaInicio();
//            Date fechaFinEvento = eventoCicloAcademico.getFechaFin();
//            if (fechaInicio != null) {
//                if (fechaInicioEvento.before(fechaInicio)) {
//                    fechaInicio = fechaInicioEvento;
//                }
//            } else {
//                fechaInicio = fechaInicioEvento;
//            }
//            if (fechaFin != null) {
//                if (fechaFinEvento.after(fechaFin)) {
//                    fechaFin = fechaFinEvento;
//                }
//            } else {
//                fechaFin = fechaFinEvento;
//            }
//        }
//
//        Date fechaInicio01 = null;
//        Date fechaFin01 = null;
//
//        Date fechaInicio02 = null;
//        Date fechaFin02 = null;
//
//        Date fechaInicio03 = null;
//        Date fechaFin03 = null;
//
//        if (fechaInicio == null) {
//            throw new PhobosException(Constantine.REQ_EVENTO);
//        }
//        if (fechaFin == null) {
//            throw new PhobosException(Constantine.REQ_EVENTO);
//        }
//
//        logger.debug(" fechaInicio {} ", new DateTime(fechaInicio).toString("dd-MM-yyyy"));
//        logger.debug(" fechaFin {} ", new DateTime(fechaFin).toString("dd-MM-yyyy"));
//
//        long inicio = fechaInicio.getTime();
//        long fin = fechaFin.getTime();
//        long rango = fin - inicio;
//        long intervalo = rango / 3;
//
//        long f1 = inicio + intervalo;
//        long f2 = inicio + intervalo * 2;
//        long f3 = inicio + intervalo * 3;
//
//        fechaInicio01 = new Date(f1 - 15 * DAYS_IN_MS);
//        fechaInicio02 = new Date(f2 - 15 * DAYS_IN_MS);
//        fechaInicio03 = new Date(f3 - 15 * DAYS_IN_MS);
//
//        fechaFin01 = new Date(f1 - 7 * DAYS_IN_MS);
//        fechaFin02 = new Date(f2 - 7 * DAYS_IN_MS);
//        fechaFin03 = new Date(f3 - 7 * DAYS_IN_MS);
//
//        Map<Integer, Map<Integer, Date>> fechas = new LinkedHashMap();
//
//        Map<Integer, Date> fm1 = new LinkedHashMap();
//        fm1.put(1, fechaInicio01);
//        fm1.put(2, fechaFin01);
//        fechas.put(1, fm1);
//
//        Map<Integer, Date> fm2 = new LinkedHashMap();
//        fm2.put(1, fechaInicio02);
//        fm2.put(2, fechaFin02);
//        fechas.put(2, fm2);
//
//        Map<Integer, Date> fm3 = new LinkedHashMap();
//        fm3.put(1, fechaInicio03);
//        fm3.put(2, fechaFin03);
//        fechas.put(3, fm3);
//        logger.debug("fechas # {}", fechas.size());
//
//        Map<Long, Integer> grupoFecha = new LinkedHashMap();
//
//        visorEncuestaDocente.iniciarConteo(profesSecciones.size());
//
//        for (DocenteSeccion docenteSeccion : profesSecciones) {
//
//            EncuestaDocente encuProfe = mapEncuestaByProfeSecc.get(docenteSeccion.getId());
//            if (encuProfe != null) {
//                visorEncuestaDocente.incrementar();
//                continue;
//            }
//
//            Docente docente = docenteSeccion.getDocente();
//            Seccion seccion = docenteSeccion.getSeccion();
//            GrupoSeccion gpoSecc = seccion.getGrupoSeccion();
//            Curso curso = gpoSecc.getCurso();
//
//            Curso cursoSinEncuesta = mapCursoNoEncuestar.get(curso.getId());
//            if (cursoSinEncuesta != null) {
//                visorEncuestaDocente.incrementar();
//                continue;
//            }
//
//            int cantidadDocentes = this.cantidadDocentes(gpoSecc, mapProfeSeccByGpoSecc);
//            boolean cumpleCantMaxDocentes = this.excedeCantidadMaximaDocentes(gpoSecc, maximoDocentes, mapProfeSeccByGpoSecc);
//            boolean esDocentePractica = this.esDocentePractica(gpoSecc, mapMapSeccByGpoSecc, seccion, mapProfeSeccBySeccion, docente);
//            if (cantidadDocentes != 1) {
//                if (cantidadDocentes < 1) {
//                    visorEncuestaDocente.incrementar();
//                    continue;
//                }
//                if (cantidadDocentes > 3) {
//                    this.makeEncuestaDocente(seccion, minimoAlumnos, ds, docenteSeccion, encuestaEstudiantil,
//                            mapMatriSeccBySeccion, fechaInicio03, fechaFin03, cumpleCantMaxDocentes, esDocentePractica);
//                    visorEncuestaDocente.incrementar();
//                    continue;
//                }
//
//                Integer ordenAsignacion = grupoFecha.get(gpoSecc.getId());
//                if (ordenAsignacion == null) {
//                    ordenAsignacion = 1;
//                }
//                if (ordenAsignacion > 3) {
//                    //logger.debug("orden sobrepasado para docente {} del grupo {} ", docente.getId(), grupo.getId());
//                    ordenAsignacion = 1;
//                }
//                //logger.debug("cantidad docentes {}", cantidadDocentes);
//                //logger.debug("grupo orden {}", ordenAsignacion);
//                Map<Integer, Date> gFecha = fechas.get(ordenAsignacion);
//                ordenAsignacion++;
//                grupoFecha.put(gpoSecc.getId(), ordenAsignacion);
//                Date fInicio = gFecha.get(1);
//                Date fFin = gFecha.get(2);
//                //logger.debug("grupo de tres {}  {}  {} ", grupo.getId(), new DateTime(fInicio).toString("dd-MM-yyyy"), new DateTime(fFin).toString("dd-MM-yyyy"));
//                this.makeEncuestaDocente(seccion, minimoAlumnos, ds, docenteSeccion, encuestaEstudiantil,
//                        mapMatriSeccBySeccion, fInicio, fFin, cumpleCantMaxDocentes, esDocentePractica);
//
//                visorEncuestaDocente.incrementar();
//                continue;
//            }
//
//            for (EventoCicloAcademico eventoCicloAcademico : eventosCicloAcademico) {
//                Date fechaFinEvento = eventoCicloAcademico.getFechaFin();
//                Date fechaInicio1 = new Date(fechaFinEvento.getTime() - 15 * DAYS_IN_MS);
//                Date fechaFin1 = new Date(fechaFinEvento.getTime() - 7 * DAYS_IN_MS);
//                this.makeEncuestaDocente(seccion, minimoAlumnos, ds, docenteSeccion, encuestaEstudiantil,
//                        mapMatriSeccBySeccion, fechaInicio1, fechaFin1, cumpleCantMaxDocentes, esDocentePractica);
//            }
//            visorEncuestaDocente.incrementar();
//        }
//    }
//
//    private boolean esDocentePractica(GrupoSeccion grupo, Map<Long, Map<Long, Seccion>> grupoSeccionPorGrupo,
//            Seccion seccion, Map<Long, List<DocenteSeccion>> docenteSeccionPorSeccion, Docente docente) {
//        Map<Long, Seccion> seccionMap = grupoSeccionPorGrupo.get(grupo.getId());
//        if (seccion.getTipoSeccionEnum() != TipoSeccionEnum.PCUR) {
//            return false;
//        }
//        Docente docenteTeoria = this.getDocenteTeoria(seccionMap, docenteSeccionPorSeccion);
//        if (docenteTeoria.getId() == docente.getId().longValue()) {
//            return true;
//        }
//        return false;
//    }
//
//    private Docente getDocenteTeoria(Map<Long, Seccion> seccionMap, Map<Long, List<DocenteSeccion>> docenteSeccionMap) {
//        if (seccionMap == null || seccionMap.isEmpty()) {
//            return new Docente(0);
//        }
//        Seccion seccionTeoria = this.getSeccionTeoria(seccionMap);
//        if (seccionTeoria == null) {
//            return new Docente(0);
//        }
//        List<DocenteSeccion> docs = docenteSeccionMap.get(seccionTeoria.getId());
//        for (DocenteSeccion doc : docs) {
//            if (doc.getSeccion().getTipoSeccionEnum() == TipoSeccionEnum.TCUR) {
//                return doc.getDocente();
//            }
//        }
//        return new Docente(0);
//    }
//
//    private Seccion getSeccionTeoria(Map<Long, Seccion> seccionMap) {
//        for (Seccion seccion : seccionMap.values()) {
//            if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.TCUR) {
//                return seccion;
//            }
//        }
//        return null;
//    }
//
//    private void makeEncuestaDocente(
//            Seccion seccion,
//            Long minimoAlumnos,
//            DataSessionPivot ds,
//            DocenteSeccion docenteSeccion,
//            EncuestaEstudiantil encuestaEstudiantil,
//            Map<Long, List<MatriculaSeccion>> matriculaSeccionPorSeccion,
//            Date fechaInicio,
//            Date fechaFin,
//            boolean cumpleCantMaxDocentes,
//            boolean esDocentePractica) {
//
//        EncuestaDocente encuestaDocente = new EncuestaDocente();
//        encuestaDocente.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
//
//        if (esDocentePractica) {
//            //logger.debug("{}", Constantine.REQ_CUR_TEORIA);
//            encuestaDocente.setDescripcion(Constantine.REQ_CUR_TEORIA);
//            encuestaDocente.setEstadoEnum(EncuestaEstudiantilEstadoEnum.TEO);
//        }
//
//        if (cumpleCantMaxDocentes) {
//            //logger.debug("{}", Constantine.REQ_MAX_DOCENTE);
//            encuestaDocente.setDescripcion(Constantine.REQ_MAX_DOCENTE);
//            encuestaDocente.setEstadoEnum(EncuestaEstudiantilEstadoEnum.INA);
//        }
//
//        encuestaDocente.setFechaEncuestaInicio(fechaInicio);
//        encuestaDocente.setFechaEncuestaFin(fechaFin);
//        encuestaDocente.setDocenteSeccion(docenteSeccion);
//        encuestaDocente.setEncuestaEstudiantil(encuestaEstudiantil);
//        encuestaDocente.setAlumnosFin(0L);
//        encuestaDocente.setAlumnosInicio(0L);
//        encuestaDocente.setAlumnosEncuestados(0L);
//        encuestaDocente.setEsTeoriaPractica(0);
//        encuestaDocenteDAO.save(encuestaDocente);
//        this.makeEncuestaAlumno(matriculaSeccionPorSeccion, encuestaDocente, seccion, ds, minimoAlumnos);
//
//        encuestaEstudiantil.setObjetivosEncuesta(encuestaEstudiantil.getObjetivosEncuesta() + 1);
//        encuestaEstudiantilDAO.update(encuestaEstudiantil);
//    }
//
//    private void makeEncuestaAlumno(
//            Map<Long, List<MatriculaSeccion>> matriculaSeccionMap,
//            EncuestaDocente encuestaDocente,
//            Seccion seccion,
//            DataSessionPivot ds,
//            Long minimoAlumnos) {
//
//        List<MatriculaSeccion> matriculaSeccion = matriculaSeccionMap.get(seccion.getId());
//        if (matriculaSeccion == null) {
//            //logger.debug("1 {}", Constantine.REQ_MIN_ALUMNO);
//            encuestaDocente.setDescripcion(Constantine.REQ_MIN_ALUMNO);
//            encuestaDocente.setEstadoEnum(EncuestaEstudiantilEstadoEnum.INA);
//            encuestaDocenteDAO.update(encuestaDocente);
//            return;
//        }
//
//        Map<Long, Alumno> alumnos = TypesUtil.convertListToMap("matriculaResumen.alumno.id", "matriculaResumen.alumno", matriculaSeccion);
//        if (alumnos == null || alumnos.isEmpty()) {
//            // logger.debug("2 {}", Constantine.REQ_MIN_ALUMNO);
//            encuestaDocente.setDescripcion(Constantine.REQ_MIN_ALUMNO);
//            encuestaDocente.setEstadoEnum(EncuestaEstudiantilEstadoEnum.INA);
//            encuestaDocenteDAO.update(encuestaDocente);
//            return;
//        }
//
//        if (alumnos.size() < minimoAlumnos) {
//            //logger.debug("3 {}", Constantine.REQ_MIN_ALUMNO);
//            encuestaDocente.setDescripcion(Constantine.REQ_MIN_ALUMNO);
//            encuestaDocente.setEstadoEnum(EncuestaEstudiantilEstadoEnum.INA);
//        }
//
//        for (Alumno alumno : alumnos.values()) {
//            EncuestaAlumno encuesta = new EncuestaAlumno();
//            encuesta.setAlumno(alumno);
//            encuesta.setEncuestaDocente(encuestaDocente);
//            if (encuestaDocente.getEstadoEnum() == EncuestaEstudiantilEstadoEnum.ACT) {
//                encuesta.setEstadoEnum(EncuestaEstudiantilEstadoEnum.PEND);
//            } else {
//                encuesta.setEstadoEnum(EncuestaEstudiantilEstadoEnum.INA);
//            }
//            encuesta.setUserRegistro(ds.getUsuario());
//            encuesta.setFechaRegistro(new Date());
//            encuestaAlumnoDAO.save(encuesta);
//        }
//        encuestaDocente.setAlumnosInicio(new Long(alumnos.size()));
//        encuestaDocente.setAlumnosFin(new Long(alumnos.size()));
//        encuestaDocenteDAO.update(encuestaDocente);
//    }
//
//    private boolean excedeCantidadMaximaDocentes(
//            GrupoSeccion gpoSecc,
//            Long maximoDocentes,
//            Map<Long, List<DocenteSeccion>> mapProfeSeccByGpoSecc) {
//
//        List<DocenteSeccion> profeSecc = mapProfeSeccByGpoSecc.get(gpoSecc.getId());
//        if (profeSecc == null || profeSecc.isEmpty()) {
//            return true;
//        }
//
//        Map<Long, Docente> docentes = new LinkedHashMap();
//        for (DocenteSeccion docenteSeccion : profeSecc) {
//            Docente docente = docenteSeccion.getDocente();
//            docentes.put(docente.getId(), docente);
//        }
//        //logger.debug("# docs {} grupo {} ", docentes.size(), grupo.getId());
//        if (docentes.size() > maximoDocentes) {
//            return true;
//        }
//        return false;
//    }
//
//    private int cantidadDocentes(GrupoSeccion gpoSecc, Map<Long, List<DocenteSeccion>> mapProfeSeccByGpoSecc) {
//        List<DocenteSeccion> docentesSeccion = mapProfeSeccByGpoSecc.get(gpoSecc.getId());
//        if (docentesSeccion == null || docentesSeccion.isEmpty()) {
//            return 0;
//        }
//        Map<Long, Docente> docentes = new LinkedHashMap();
//        for (DocenteSeccion docenteSeccion : docentesSeccion) {
//            Docente docente = docenteSeccion.getDocente();
//            docentes.put(docente.getId(), docente);
//        }
//        return docentes.size();
//    }
}
