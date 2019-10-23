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
import pe.edu.lamolina.model.enums.EncuestaEstadoEnum;
import pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.TemaExamenVirtual;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
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
    CursoDAO cursoDAO;

    @Autowired
    VisorEncuestaDocente visorEncuestaDocente;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Async
    @Override
    @Transactional
    public void generarEncuesta(CicloAcademico ciclo, DataSessionPivot ds) {
        visorEncuestaDocente.setEstado("Obteniendo información de matriculados");
        List<MatriculaSeccion> matriculasSecciones = matriculaSeccionDAO.allMatriculadosByCiclo(ciclo);
        Map<Long, List<Alumno>> mapAlumnos = TypesUtil.convertListToMapList("seccion.id", "matriculaResumen.alumno", matriculasSecciones);
        for (Map.Entry<Long, List<Alumno>> entry : mapAlumnos.entrySet()) {
            List<Alumno> alumnos = clearAlumnosDuplicados(entry.getValue());
            mapAlumnos.put(entry.getKey(), alumnos);
        }

        //ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        visorEncuestaDocente.setEstado("Obteniendo información de docentes-secciones");
        List<DocenteSeccion> profesPersonasSecciones = docenteSeccionDAO.allSinNNByCicloModalidad(ciclo);
        List<DocenteSeccion> profesActivosSecciones = docenteSeccionDAO.allActivosByCiclo(ciclo);
        List<EncuestaDocenteModalidad> encusProfesModalidadades = encuestaDocenteModalidadDAO.allByCiclo(ciclo);
        Map<Long, List<DocenteSeccion>> mapProfeSeccBySecc = TypesUtil.convertListToMapList("seccion.id", profesActivosSecciones);
        Map<Long, List<DocenteSeccion>> mapProfeSeccByGpoSecc = TypesUtil.convertListToMapList("seccion.grupoSeccion.id", profesActivosSecciones);
        Map<String, EncuestaDocenteModalidad> mapEncusProfesModalidadades = TypesUtil.convertListToMap("key", encusProfesModalidadades);

        EncuestaEstudiantil encuestaDocente = encuestaEstudiantilDAO.findByCicloTipo(ciclo, TipoExamenVirtualEnum.ENC_DOC);
        ConfiguraEncuesta configuraEncuesta = configuraEncuestaDAO.findByEncuesta(encuestaDocente);
        List<PeriodoEncuesta> periodosEncuesta = periodoEncuestaDAO.allByEncuesta(encuestaDocente);
        List<TemaExamenVirtual> temas = temaExamenVirtualDAO.allByEvaluacion(encuestaDocente.getEncuesta());
        encuestaDocente.getEncuesta().setTema(temas);

        List<EncuestaDocente> encuestasDocentes = encuestaDocenteDAO.allByEncuestaEstudiantil(encuestaDocente);
        Map<Long, EncuestaDocente> mapEncuestaByProfeSecc = TypesUtil.convertListToMap("docenteSeccion.id", encuestasDocentes);

        List<CursoSinEncuesta> cursosSinEncuesta = cursoSinEncuestaDAO.allByEncuestaEstudiantil(encuestaDocente);
        Map<Long, Curso> mapCursosSinEncuesta = TypesUtil.convertListToMap("curso.id", "curso", cursosSinEncuesta);

        visorEncuestaDocente.iniciarConteo(profesPersonasSecciones.size());
        for (DocenteSeccion profeSecc : profesPersonasSecciones) {
            saveEncuestaDocente(
                    profeSecc,
                    mapAlumnos,
                    mapProfeSeccBySecc,
                    mapProfeSeccByGpoSecc,
                    mapEncuestaByProfeSecc,
                    mapCursosSinEncuesta,
                    mapEncusProfesModalidadades,
                    configuraEncuesta,
                    periodosEncuesta,
                    encuestaDocente, ds);
            visorEncuestaDocente.incrementar();
        }

        encuestaEstudiantilDAO.update(encuestaDocente);
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
        GrupoSeccion gpoSeccion = profeSecc.getSeccion().getGrupoSeccion();
        List<DocenteSeccion> profesoresSecc = mapProfeSeccBySeccion.get(seccion.getId());
        List<Alumno> alumnos = mapAlumnos.get(seccion.getId());
        alumnos = (alumnos == null) ? new ArrayList() : alumnos;

        Docente docente = profeSecc.getDocente();
        Curso curso = gpoSeccion.getCurso();
        ModalidadEstudio modalidad = curso.getModalidadEstudio();
        CicloAcademico ciclo = profeSecc.getSeccion().getGrupoSeccion().getCicloAcademico();

        EncuestaDocenteModalidad encuProfeModalidad = mapEncusProfesModalidadades.get(docente.getId() + "-" + modalidad.getId());
        logger.debug("encprofmoda {}", encuProfeModalidad);
        logger.debug("KEY {}", docente.getId() + "-" + modalidad.getId());
        if (encuProfeModalidad == null) {
            encuProfeModalidad = new EncuestaDocenteModalidad();
            encuProfeModalidad.setCicloAcademico(ciclo);
            encuProfeModalidad.setModalidadEstudio(modalidad);
            encuProfeModalidad.setDocente(docente);
            encuProfeModalidad.setAlumnosEncuestados(0);
            encuProfeModalidad.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ANU);
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
                puntaje.setDesviacionStandar(BigDecimal.ZERO);
                puntajeEncuestaDocenteModalidadDAO.save(puntaje);
            }
        }

        String impedido = null;
        if (profesoresSecc.size() > configuraEncuesta.getCantidadMaximaDocentes()) {
            impedido = "Anulada porque excede la cantidad máxima de docentes. ";
        }

        if (gpoSeccion.getCursoDirigido()) {
            impedido = "Anulada porque es Curso Dirigido. ";
        }

        if (alumnos.size() < configuraEncuesta.getCantidadMinimaAlumnosPregrado() && modalidad.isPregrado()) {
            impedido = (impedido == null ? "" : impedido);
            impedido += "Anulada porque no tiene la cantidad mínima de alumnos. ";
        }

        if (alumnos.size() < configuraEncuesta.getCantidadMinimaAlumnosPosgrado() && modalidad.isPostgrado()) {
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
            enc.setModalidadEstudio(profeSecc.getSeccion().getGrupoSeccion().getCurso().getModalidadEstudio());
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
                enc.setModalidadEstudio(profeSecc.getSeccion().getGrupoSeccion().getCurso().getModalidadEstudio());
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
                    enc.setModalidadEstudio(profeSecc.getSeccion().getGrupoSeccion().getCurso().getModalidadEstudio());
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
                enc.setModalidadEstudio(profeSecc.getSeccion().getGrupoSeccion().getCurso().getModalidadEstudio());
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
            enc.setModalidadEstudio(profeSecc.getSeccion().getGrupoSeccion().getCurso().getModalidadEstudio());
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
        enc.setModalidadEstudio(profeSecc.getSeccion().getGrupoSeccion().getCurso().getModalidadEstudio());
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

}
