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
import org.thymeleaf.util.StringUtils;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AnexoBoletin;
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
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaCurso;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocenteModalidad;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.encuestaestudiantil.PeriodoEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocenteModalidad;
import pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.ANU;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.ENC;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.PEND;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import static pe.edu.lamolina.model.enums.TipoDictadoGrupoSeccionEnum.MOD;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.TemaExamenVirtual;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.encuesta.ConfiguraEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.CursoSinEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaAlumnoDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaCursoDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaDocenteDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaDocenteModalidadDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaEstudiantilDAO;
import pe.edu.lamolina.pivot.dao.encuesta.PeriodoEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.PuntajeEncuestaDocenteModalidadDAO;
import pe.edu.lamolina.pivot.dao.encuesta.TemaExamenVirtualDAO;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
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
    EncuestaCursoDAO encuestaCursoDAO;

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

        visorEncuestaDocente.setEstado("Obteniendo información de docentes-secciones");
        List<EncuestaAlumno> encuestasAlumnos = new ArrayList();

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

        List<EncuestaDocente> encuestasDocentes = encuestaDocenteDAO.allByEncuestaEstudiantil(encuestaDocente, new ArrayList());
        Map<Long, EncuestaDocente> mapEncuestaByProfeSecc = TypesUtil.convertListToMap("docenteSeccion.id", encuestasDocentes);

        List<CursoSinEncuesta> cursosSinEncuesta = cursoSinEncuestaDAO.allByEncuestaEstudiantil(encuestaDocente);
        Map<Long, Curso> mapCursosSinEncuesta = TypesUtil.convertListToMap("curso.id", "curso", cursosSinEncuesta);

        List<ModalidadEstudio> modalidades = modalidadEstudioDAO.allPrePostgrado(new Compania(1L));
        Map<String, ModalidadEstudio> mapModalidad = TypesUtil.convertListToMap("codigo", modalidades);

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
                    encuestaDocente,
                    encuestasAlumnos,
                    mapModalidad, ds);
            visorEncuestaDocente.incrementar();
        }

        encuestaEstudiantilDAO.update(encuestaDocente);
        saveEncuestaAlumno(null, new ArrayList(), encuestasAlumnos, ds);
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
            List<EncuestaAlumno> encuestasAlumnos,
            Map<String, ModalidadEstudio> mapModalidad,
            DataSessionPivot ds) {

        EncuestaDocente encuProfe = mapEncuestaByProfeSecc.get(profeSecc.getId());
        if (encuProfe != null) {
            return;
        }

        Seccion seccion = profeSecc.getSeccion();
        GrupoSeccion gpoSeccion = seccion.getGrupoSeccion();
        AnexoBoletin anexoSup = gpoSeccion.getAnexoBoletin().getAnexoSuperior();
        List<DocenteSeccion> profesoresSecc = mapProfeSeccBySeccion.get(seccion.getId());
        List<Alumno> alumnos = TypesUtil.getListNotNull(mapAlumnos.get(seccion.getId()));

        Docente docente = profeSecc.getDocente();
        Curso curso = gpoSeccion.getCurso();
        ModalidadEstudioEnum modaEnum = anexoSup.isAnexoCursosPostgrado() ? ModalidadEstudioEnum.EPG : ModalidadEstudioEnum.PRE;
        ModalidadEstudio modalidad = mapModalidad.get(modaEnum.name());
        CicloAcademico ciclo = profeSecc.getSeccion().getGrupoSeccion().getCicloAcademico();

        EncuestaDocenteModalidad encuProfeModalidad = mapEncusProfesModalidadades.get(docente.getId() + "-" + modalidad.getId());
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

        if (alumnos.size() < configuraEncuesta.getCantidadMinimaAlumnosPregrado() && !anexoSup.isAnexoCursosPostgrado()) {
            impedido = (impedido == null ? "" : impedido);
            impedido += "Anulada porque no tiene la cantidad mínima de alumnos pregrados. ";
        }

        if (alumnos.size() < configuraEncuesta.getCantidadMinimaAlumnosPosgrado() && anexoSup.isAnexoCursosPostgrado()) {
            impedido = (impedido == null ? "" : impedido);
            impedido += "Anulada porque no tiene la cantidad mínima de alumnos posgrados. ";
        }

        Curso cursoNoEnc = mapCursosNoEncuestar.get(curso.getId());
        if (cursoNoEnc != null) {
            impedido = (impedido == null ? "" : impedido);
            impedido += "Anulada porque este curso está configurado para no ser encuestado. ";
        }

        if (impedido != null) {
            EncuestaDocente enc = new EncuestaDocente();
            enc.setModalidadEstudio(modalidad);
            enc.setAlumnosEncuestados(0L);
            enc.setAlumnosInicio(Long.valueOf(alumnos.size()));
            enc.setAlumnosFin(Long.valueOf(alumnos.size()));
            enc.setDescripcion(impedido);
            enc.setDocenteSeccion(profeSecc);
            enc.setEncuestaEstudiantil(encuestaEstudiantil);
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
                enc.setModalidadEstudio(modalidad);
                enc.setAlumnosEncuestados(0L);
                enc.setAlumnosInicio(Long.valueOf(alumnos.size()));
                enc.setAlumnosFin(Long.valueOf(alumnos.size()));
                enc.setDescripcion(impedido);
                enc.setDocenteSeccion(profeSecc);
                enc.setEncuestaEstudiantil(encuestaEstudiantil);
                enc.setEsTeoriaPractica(configuraEncuesta.getEncuestaTeoriaPractica().intValue());
                enc.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
                enc.setUserRegistro(ds.getUsuario());
                enc.setFechaRegistro(new Date());

                if (gpoSeccion.getTipoDictadoEnum() == MOD) {
                    enc.setFechaEncuestaInicio(getFechaInicioEncuesta(profeSecc, configuraEncuesta));
                    enc.setFechaEncuestaFin(profeSecc.getFechaFin());
                } else {
                    enc.setFechaEncuestaInicio(periodo.getFechaInicio());
                    enc.setFechaEncuestaFin(periodo.getFechaFin());
                }

                encuestaDocenteDAO.save(enc);

                saveEncuestaAlumno(encuProfe, alumnos, encuestasAlumnos, ds);
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
                    enc.setModalidadEstudio(modalidad);
                    enc.setAlumnosEncuestados(0L);
                    enc.setAlumnosInicio(Long.valueOf(alumnos.size()));
                    enc.setAlumnosFin(Long.valueOf(alumnos.size()));
                    enc.setDocenteSeccion(profeSecc);
                    enc.setEncuestaEstudiantil(encuestaEstudiantil);
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
                enc.setModalidadEstudio(modalidad);
                enc.setAlumnosEncuestados(0L);
                enc.setAlumnosInicio(Long.valueOf(alumnos.size()));
                enc.setAlumnosFin(Long.valueOf(alumnos.size()));
                enc.setDescripcion(impedido);
                enc.setDocenteSeccion(profeSecc);
                enc.setEncuestaEstudiantil(encuestaEstudiantil);
                enc.setEsTeoriaPractica(configuraEncuesta.getEncuestaTeoriaPractica().intValue());
                enc.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
                enc.setUserRegistro(ds.getUsuario());
                enc.setFechaRegistro(new Date());

                if (gpoSeccion.getTipoDictadoEnum() == MOD) {
                    enc.setFechaEncuestaInicio(getFechaInicioEncuesta(profeSecc, configuraEncuesta));
                    enc.setFechaEncuestaFin(profeSecc.getFechaFin());
                } else {
                    enc.setFechaEncuestaInicio(periodo.getFechaInicio());
                    enc.setFechaEncuestaFin(periodo.getFechaFin());
                }

                encuestaDocenteDAO.save(enc);

                saveEncuestaAlumno(enc, alumnos, encuestasAlumnos, ds);
                encuestaEstudiantil.setObjetivosEncuesta(encuestaEstudiantil.getObjetivosEncuesta() + 1);
                encuestaEstudiantil.setEncuestasProgramadas(encuestaEstudiantil.getEncuestasProgramadas() + alumnos.size());

                encuProfeModalidad.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
                encuestaDocenteModalidadDAO.update(encuProfeModalidad);
            }
            return;
        }

        if (profeSecc.getFechaFin() == null) {
            EncuestaDocente enc = new EncuestaDocente();
            enc.setModalidadEstudio(modalidad);
            enc.setAlumnosEncuestados(0L);
            enc.setAlumnosInicio(Long.valueOf(alumnos.size()));
            enc.setAlumnosFin(Long.valueOf(alumnos.size()));
            enc.setDescripcion(impedido);
            enc.setDocenteSeccion(profeSecc);
            enc.setEncuestaEstudiantil(encuestaEstudiantil);
            enc.setEsTeoriaPractica(0);
            enc.setEstadoEnum(EncuestaEstudiantilEstadoEnum.FECH);
            enc.setDescripcion("No se encuesta porque el docente no tiene configurado su periodo de clases");
            enc.setUserRegistro(ds.getUsuario());
            enc.setFechaRegistro(new Date());
            encuestaDocenteDAO.save(enc);
            return;
        }

        EncuestaDocente enc = new EncuestaDocente();
        enc.setModalidadEstudio(modalidad);
        enc.setAlumnosEncuestados(0L);
        enc.setAlumnosInicio(Long.valueOf(alumnos.size()));
        enc.setAlumnosFin(Long.valueOf(alumnos.size()));
        enc.setDescripcion(impedido);
        enc.setDocenteSeccion(profeSecc);
        enc.setEncuestaEstudiantil(encuestaEstudiantil);
        enc.setEsTeoriaPractica(configuraEncuesta.getEncuestaTeoriaPractica().intValue());
        enc.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
        enc.setFechaEncuestaInicio(getFechaInicioEncuesta(profeSecc, configuraEncuesta));
        enc.setFechaEncuestaFin(profeSecc.getFechaFin());
        enc.setUserRegistro(ds.getUsuario());
        enc.setFechaRegistro(new Date());
        encuestaDocenteDAO.save(enc);

        saveEncuestaAlumno(enc, alumnos, encuestasAlumnos, ds);
        encuestaEstudiantil.setObjetivosEncuesta(encuestaEstudiantil.getObjetivosEncuesta() + 1);
        encuestaEstudiantil.setEncuestasProgramadas(encuestaEstudiantil.getEncuestasProgramadas() + alumnos.size());

        encuProfeModalidad.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
        encuestaDocenteModalidadDAO.update(encuProfeModalidad);
    }

    private Date getFechaInicioEncuesta(DocenteSeccion profeSecc, ConfiguraEncuesta configuraEncuesta) {
        Date inicioEncuesta = new DateTime(profeSecc.getFechaFin()).minusDays(configuraEncuesta.getDiasEncuesta().intValue()).toDate();
        if (inicioEncuesta.compareTo(profeSecc.getFechaInicio()) > 0) {
            return inicioEncuesta;
        }
        return profeSecc.getFechaInicio();

    }

    private void saveEncuestaAlumno(
            EncuestaDocente encuestaDocente,
            List<Alumno> alumnos,
            List<EncuestaAlumno> encuestasAlumnos,
            DataSessionPivot ds) {

        for (Alumno alumno : alumnos) {
            EncuestaAlumno encuAlumno = new EncuestaAlumno();
            encuAlumno.setAlumno(alumno);
            encuAlumno.setEncuestaDocente(encuestaDocente);
            encuAlumno.setEstadoEnum(EncuestaEstudiantilEstadoEnum.PEND);
            encuAlumno.setUserRegistro(ds.getUsuario());
            encuAlumno.setFechaRegistro(new Date());
            //encuestaAlumnoDAO.save(encuAlumno);
            encuestasAlumnos.add(encuAlumno);
        }

        if (encuestasAlumnos.size() > 2000) {
            encuestaAlumnoDAO.saveList(encuestasAlumnos);
            encuestasAlumnos.clear();
        }

        if (encuestaDocente == null) {
            encuestaAlumnoDAO.saveList(encuestasAlumnos);
            encuestasAlumnos.clear();
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

    @Override
    @Transactional
    public void generarEncuestaDocente(DocenteSeccion docenteSeccionForm, CicloAcademico ciclo, DataSessionPivot ds) {
        DocenteSeccion docenteSeccionBD = docenteSeccionDAO.find(docenteSeccionForm.getId());
        List<MatriculaSeccion> matriculasSecciones = matriculaSeccionDAO.allMatriculadosBySeccion(docenteSeccionBD.getSeccion());
        Map<Long, List<Alumno>> mapAlumnos = TypesUtil.convertListToMapList("seccion.id", "matriculaResumen.alumno", matriculasSecciones);
        for (Map.Entry<Long, List<Alumno>> entry : mapAlumnos.entrySet()) {
            List<Alumno> alumnos = clearAlumnosDuplicados(entry.getValue());
            mapAlumnos.put(entry.getKey(), alumnos);
        }

        Docente docente = docenteSeccionBD.getDocente();
        boolean esNN = docente.getCodigo().equals(AcademicoConstantine.DOCENTE_INDETERMINADO);
        Assert.isFalse(esNN, "No se genera encuestas para docentes N.N.");

        List<EncuestaAlumno> encuestasAlumnos = new ArrayList();

        List<DocenteSeccion> profesPersonasSecciones = new ArrayList();
        profesPersonasSecciones.add(docenteSeccionBD);

        List<DocenteSeccion> profesActivosSecciones = docenteSeccionDAO.allActivosByCiclo(ciclo);
        List<EncuestaDocenteModalidad> encusProfesModalidadades = encuestaDocenteModalidadDAO.allByDocenteCiclo(docente, ciclo);
        Map<Long, List<DocenteSeccion>> mapProfeSeccBySecc = TypesUtil.convertListToMapList("seccion.id", profesActivosSecciones);
        Map<Long, List<DocenteSeccion>> mapProfeSeccByGpoSecc = TypesUtil.convertListToMapList("seccion.grupoSeccion.id", profesActivosSecciones);
        Map<String, EncuestaDocenteModalidad> mapEncusProfesModalidadades = TypesUtil.convertListToMap("key", encusProfesModalidadades);

        EncuestaEstudiantil encuestaDocente = encuestaEstudiantilDAO.findByCicloTipo(ciclo, TipoExamenVirtualEnum.ENC_DOC);
        ConfiguraEncuesta configuraEncuesta = configuraEncuestaDAO.findByEncuesta(encuestaDocente);
        List<PeriodoEncuesta> periodosEncuesta = periodoEncuestaDAO.allByEncuesta(encuestaDocente);
        List<TemaExamenVirtual> temas = temaExamenVirtualDAO.allByEvaluacion(encuestaDocente.getEncuesta());
        encuestaDocente.getEncuesta().setTema(temas);

        List<EncuestaDocente> encuestasDocentes = encuestaDocenteDAO.allByEncuestaEstudiantil(encuestaDocente, new ArrayList());
        Map<Long, EncuestaDocente> mapEncuestaByProfeSecc = TypesUtil.convertListToMap("docenteSeccion.id", encuestasDocentes);

        List<CursoSinEncuesta> cursosSinEncuesta = cursoSinEncuestaDAO.allByEncuestaEstudiantil(encuestaDocente);
        Map<Long, Curso> mapCursosSinEncuesta = TypesUtil.convertListToMap("curso.id", "curso", cursosSinEncuesta);

        List<ModalidadEstudio> modalidades = modalidadEstudioDAO.allPrePostgrado(new Compania(1L));
        Map<String, ModalidadEstudio> mapModalidad = TypesUtil.convertListToMap("codigo", modalidades);

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
                    encuestaDocente,
                    encuestasAlumnos,
                    mapModalidad, ds);
            visorEncuestaDocente.incrementar();
        }

        encuestaEstudiantilDAO.update(encuestaDocente);
        saveEncuestaAlumno(null, new ArrayList(), encuestasAlumnos, ds);

        EncuestaDocente encuestaDocenteDB = encuestaDocenteDAO.findByDocenteSeccion(docenteSeccionBD);
        crearEncuestaCurso(encuestaDocenteDB, ciclo, ds);
    }

    private void crearEncuestaCurso(EncuestaDocente encuestaDocenteBD, CicloAcademico ciclo, DataSessionPivot ds) {
        Seccion seccion = encuestaDocenteBD.getDocenteSeccion().getSeccion();
        GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();

        List<MatriculaSeccion> matriculadosSeccion = matriculaSeccionDAO.allMatriculadosBySeccion(seccion);
        Assert.isFalse(matriculadosSeccion.isEmpty(), "No existe alumnos matriculados en esta sección");
        Map<Long, MatriculaSeccion> mapMatriculado = TypesUtil.convertListToMap("matriculaResumen.alumno.id", matriculadosSeccion);

        EncuestaEstudiantil encuCurso = encuestaEstudiantilDAO.findByCicloTipo(ciclo, TipoExamenVirtualEnum.ENC_CUR);
        if (encuCurso == null) {
            System.out.println("111");
            return;
        }
        if (encuCurso.getObjetivosEncuesta() == 0) {
            System.out.println("222");
            return;
        }

        ConfiguraEncuesta cfgEncuestaCurso = configuraEncuestaDAO.findByEncuesta(encuCurso);
        if (cfgEncuestaCurso == null) {
            System.out.println("333");
            return;
        }
        if (cfgEncuestaCurso.getSimultaneo() == null) {
            System.out.println("4444");
            return;
        }
        if (cfgEncuestaCurso.getSimultaneo() != 1) {
            System.out.println("5555");
            return;
        }

        int total = matriculadosSeccion.size();
        int reprogramadas = 0;

        EncuestaCurso encuestaCurso = encuestaCursoDAO.findByEncuestaDocente(encuestaDocenteBD);

        boolean yaExiste = true;
        if (encuestaCurso == null) {
            System.out.println("6666");
            encuestaCurso = new EncuestaCurso();
            encuestaCurso.setEncuestaEstudiantil(encuCurso);
            encuestaCurso.setGrupoSeccion(grupoSeccion);
            encuestaCurso.setModalidadEstudio(encuestaDocenteBD.getModalidadEstudio());
            encuestaCurso.setEncuestaDocente(encuestaDocenteBD);

            encuestaCurso.setAlumnosFinInteger(matriculadosSeccion.size());
            encuestaCurso.setAlumnosInicioInteger(matriculadosSeccion.size());
            encuestaCurso.setAlumnosEncuestados(0L);
            encuestaCurso.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
            encuestaCurso.setFechaEncuestaInicio(encuestaDocenteBD.getFechaInicio());
            encuestaCurso.setFechaEncuestaFin(encuestaDocenteBD.getFechaFin());
            encuestaCurso.setUserRegistro(ds.getUsuario());
            encuestaCurso.setFechaRegistro(new Date());
            encuestaCursoDAO.save(encuestaCurso);
            yaExiste = false;
        }

        List<EncuestaAlumno> encuestasAlumnos = encuestaAlumnoDAO.allByEncuestaCurso(encuestaCurso);
        Map<Long, EncuestaAlumno> mapEncuAlumno = TypesUtil.convertListToMap("alumno.id", encuestasAlumnos);
        System.out.println("777");

        boolean yaEstaActiva = false;
        if (yaExiste) {
            yaEstaActiva = encuestaCurso.getEstadoEnum() == ACT;
        }

        for (MatriculaSeccion matriculado : matriculadosSeccion) {
            Alumno alumno = matriculado.getMatriculaResumen().getAlumno();
            EncuestaAlumno encuAlumno = mapEncuAlumno.get(alumno.getId());
            if (encuAlumno == null) {
                encuAlumno = new EncuestaAlumno();
                encuAlumno.setAlumno(alumno);
                encuAlumno.setEncuestaDocente(encuestaDocenteBD);
                encuAlumno.setEncuestaCurso(encuestaCurso);
                encuAlumno.setEstadoEnum(EncuestaEstudiantilEstadoEnum.PEND);
                encuAlumno.setUserRegistro(ds.getUsuario());
                encuAlumno.setFechaRegistro(new Date());
                encuestaAlumnoDAO.save(encuAlumno);
                reprogramadas++;

            } else {
                if (encuAlumno.getEstadoEnum() == ANU) {
                    encuAlumno.setEstadoEnum(ACT);
                    encuestaAlumnoDAO.update(encuAlumno);
                    reprogramadas++;
                }
            }
        }

        System.out.println("8888 " + matriculadosSeccion.size());

        for (EncuestaAlumno encuestaAlumno : encuestasAlumnos) {
            MatriculaSeccion matriculado = mapMatriculado.get(encuestaAlumno.getAlumno().getId());
            if (matriculado == null && encuestaAlumno.getEstadoEnum() == ENC) {
                total++;
            }
        }

        if (yaExiste) {
            encuestaCurso.setFechaEncuestaInicio(encuestaDocenteBD.getFechaEncuestaInicio());
            encuestaCurso.setFechaEncuestaFin(encuestaDocenteBD.getFechaEncuestaFin());
        }

        if (yaExiste && !yaEstaActiva) {
            encuestaCurso.setEstadoEnum(ACT);
            encuestaCurso.setAlumnosFinInteger(total);
            encuestaCurso.setUserModificacion(ds.getUsuario());
            encuestaCurso.setFechaModificacion(new Date());
        }

        encuestaCursoDAO.update(encuestaCurso);
        if (!yaEstaActiva) {
            encuCurso.setObjetivosEncuesta(encuCurso.getObjetivosEncuesta() + 1);
            encuCurso.setEncuestasProgramadas(encuCurso.getEncuestasProgramadas() + reprogramadas);
            encuestaEstudiantilDAO.update(encuCurso);
        }
    }

    @Override
    @Transactional
    public void activarEncuestaDocente(EncuestaDocente encuestaForm, CicloAcademico ciclo, DataSessionPivot ds) {
        EncuestaDocente encuestaDocenteBD = encuestaDocenteDAO.findEncuestaDocente(encuestaForm);
        Assert.isNotNull(encuestaDocenteBD, "No existe esta encuesta en la base de datos");

        Seccion seccion = encuestaDocenteBD.getDocenteSeccion().getSeccion();
        List<MatriculaSeccion> matriculadosSeccion = matriculaSeccionDAO.allMatriculadosBySeccion(seccion);
        Assert.isFalse(matriculadosSeccion.isEmpty(), "No existe alumnos matriculados en esta sección");
        Map<Long, MatriculaSeccion> mapMatriculado = TypesUtil.convertListToMap("matriculaResumen.alumno.id", matriculadosSeccion);

        int total = matriculadosSeccion.size();
        int reprogramadas = 0;

        List<EncuestaAlumno> encuestasAlumnos = encuestaAlumnoDAO.allByEncuestaDocente(encuestaDocenteBD);
        Map<Long, EncuestaAlumno> mapEncuAlumno = TypesUtil.convertListToMap("alumno.id", encuestasAlumnos);

        for (MatriculaSeccion matriculado : matriculadosSeccion) {
            Alumno alumno = matriculado.getMatriculaResumen().getAlumno();
            EncuestaAlumno encuAlumno = mapEncuAlumno.get(alumno.getId());
            if (encuAlumno == null) {
                encuAlumno = new EncuestaAlumno();
                encuAlumno.setAlumno(alumno);
                encuAlumno.setEncuestaDocente(encuestaDocenteBD);
                encuAlumno.setEstadoEnum(EncuestaEstudiantilEstadoEnum.PEND);
                encuAlumno.setUserRegistro(ds.getUsuario());
                encuAlumno.setFechaRegistro(new Date());
                encuestaAlumnoDAO.save(encuAlumno);
                reprogramadas++;

            } else {
                if (encuAlumno.getEstadoEnum() == ANU) {
                    encuAlumno.setEstadoEnum(PEND);
                    encuestaAlumnoDAO.update(encuAlumno);
                    reprogramadas++;
                }
            }
        }

        for (EncuestaAlumno encuestaAlumno : encuestasAlumnos) {
            MatriculaSeccion matriculado = mapMatriculado.get(encuestaAlumno.getAlumno().getId());
            if (matriculado == null && encuestaAlumno.getEstadoEnum() == ENC) {
                total++;
            }
        }

        encuestaDocenteBD.setEstadoEnum(ACT);
        encuestaDocenteBD.setAlumnosFin(Long.valueOf(total));
        encuestaDocenteBD.setUserModificacion(ds.getUsuario());
        encuestaDocenteBD.setFechaModificacion(new Date());
        encuestaDocenteDAO.update(encuestaDocenteBD);

        EncuestaEstudiantil encu = encuestaDocenteBD.getEncuestaEstudiantil();
        encu.setObjetivosEncuesta(encu.getObjetivosEncuesta() + 1);
        encu.setEncuestasProgramadas(encu.getEncuestasProgramadas() + reprogramadas);
        encuestaEstudiantilDAO.update(encu);

        this.crearEncuestaCurso(encuestaDocenteBD, ciclo, ds);

    }

    @Override
    @Transactional
    public void desactivarEncuestaDocente(EncuestaDocente encuestaDocenteForm, CicloAcademico ciclo, DataSessionPivot ds) {
        EncuestaDocente encuestaDocenteBD = encuestaDocenteDAO.findEncuestaDocente(encuestaDocenteForm);
        Assert.isNotNull(encuestaDocenteBD, "No existe esta encuesta en la base de datos");

        Assert.isFalse(StringUtils.isEmpty(encuestaDocenteForm.getDescripcion()), "Debe ingresar un motivo de la desactivación");
        encuestaDocenteBD.setEstadoEnum(ANU);
        encuestaDocenteBD.setDescripcion(encuestaDocenteForm.getDescripcion());
        encuestaDocenteBD.setUserModificacion(ds.getUsuario());
        encuestaDocenteBD.setFechaModificacion(new Date());

        int desprogramadas = 0;
        List<EncuestaAlumno> encuestas = encuestaAlumnoDAO.allByEncuestaDocente(encuestaDocenteBD);
        for (EncuestaAlumno encuestaAlumno : encuestas) {
            if (encuestaAlumno.getEstadoEnum() == PEND) {
                encuestaAlumno.setEstadoEnum(ANU);
                encuestaAlumnoDAO.update(encuestaAlumno);
                desprogramadas++;
            }
        }
        encuestaDocenteDAO.update(encuestaDocenteBD);

        EncuestaEstudiantil encu = encuestaDocenteBD.getEncuestaEstudiantil();
        encu.setObjetivosEncuesta(encu.getObjetivosEncuesta() - 1);
        encu.setEncuestasProgramadas(encu.getEncuestasProgramadas() - desprogramadas);
        encuestaEstudiantilDAO.update(encu);

        EncuestaCurso encuestaCurso = encuestaCursoDAO.findByEncuestaDocente(encuestaDocenteBD);
        if (encuestaCurso == null) {
            return;
        }

        if (encuestaCurso.getEstadoEnum() != ACT) {
            return;
        }

        encuestaCurso.setEstadoEnum(ANU);
        encuestaCurso.setDescripcion(encuestaDocenteForm.getDescripcion());
        encuestaCurso.setUserModificacion(ds.getUsuario());
        encuestaCurso.setFechaModificacion(new Date());
        encuestaCursoDAO.update(encuestaCurso);

        desprogramadas = 0;
        encuestas = encuestaAlumnoDAO.allByEncuestaCurso(encuestaCurso);
        for (EncuestaAlumno encuestaAlumno : encuestas) {
            if (encuestaAlumno.getEstadoEnum() == PEND) {
                encuestaAlumno.setEstadoEnum(ANU);
                encuestaAlumnoDAO.update(encuestaAlumno);
                desprogramadas++;
            }
        }

        EncuestaEstudiantil encuCurso = encuestaEstudiantilDAO.findByCicloTipo(ciclo, TipoExamenVirtualEnum.ENC_CUR);
        encuCurso.setObjetivosEncuesta(encuCurso.getObjetivosEncuesta() - 1);
        encuCurso.setEncuestasProgramadas(encuCurso.getEncuestasProgramadas() - desprogramadas);
        encuestaEstudiantilDAO.update(encuCurso);

    }

}
