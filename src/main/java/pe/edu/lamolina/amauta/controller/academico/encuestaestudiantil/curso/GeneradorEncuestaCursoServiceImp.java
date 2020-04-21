package pe.edu.lamolina.amauta.controller.academico.encuestaestudiantil.curso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.encuestaestudiantil.ConfiguraEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.CursoSinEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaAlumno;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaCurso;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.encuestaestudiantil.PeriodoEncuesta;
import pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.ACT;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.amauta.dao.academico.CursoDAO;
import pe.edu.lamolina.amauta.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.amauta.dao.encuesta.ConfiguraEncuestaDAO;
import pe.edu.lamolina.amauta.dao.encuesta.CursoSinEncuestaDAO;
import pe.edu.lamolina.amauta.dao.encuesta.EncuestaAlumnoDAO;
import pe.edu.lamolina.amauta.dao.encuesta.EncuestaCursoDAO;
import pe.edu.lamolina.amauta.dao.encuesta.EncuestaDocenteDAO;
import pe.edu.lamolina.amauta.dao.encuesta.EncuestaEstudiantilDAO;
import pe.edu.lamolina.amauta.dao.encuesta.PeriodoEncuestaDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class GeneradorEncuestaCursoServiceImp implements GeneradorEncuestaCursoService {

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;
    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;
    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;
    @Autowired
    EncuestaEstudiantilDAO encuestaEstudiantilDAO;
    @Autowired
    ConfiguraEncuestaDAO configuraEncuestaDAO;
    @Autowired
    CursoSinEncuestaDAO cursoSinEncuestaDAO;
    @Autowired
    EncuestaAlumnoDAO encuestaAlumnoDAO;
    @Autowired
    PeriodoEncuestaDAO periodoEncuestaDAO;
    @Autowired
    EncuestaCursoDAO encuestaCursoDAO;
    @Autowired
    CursoDAO cursoDAO;
    @Autowired
    EncuestaDocenteDAO encuestaDocenteDAO;

    @Autowired
    VisorEncuestaCurso visorEncuestaCurso;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Async
    @Override
    @Transactional
    public void generarEncuesta(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        visorEncuestaCurso.setEstado("Obteniendo información de matriculados");
        List<MatriculaSeccion> matriculasSecciones = matriculaSeccionDAO.allMatriculadosByCiclo(cicloAcademico);
        List<EncuestaAlumno> encuestasAlumnos = new ArrayList();

        visorEncuestaCurso.setEstado("Obteniendo información de cursos-secciones");

        Map<Long, GrupoSeccion> mapGposSeccion = TypesUtil.convertListToMap("seccion.grupoSeccion.id", "seccion.grupoSeccion", matriculasSecciones);

        EncuestaEstudiantil encuestaTipoCurso = encuestaEstudiantilDAO.findByCicloTipo(cicloAcademico, TipoExamenVirtualEnum.ENC_CUR);
        ConfiguraEncuesta configuraEncuesta = configuraEncuestaDAO.findByEncuesta(encuestaTipoCurso);

        List<ModalidadEstudio> modalidades = modalidadEstudioDAO.allPrePostgrado(new Compania(1L));
        Map<String, ModalidadEstudio> mapModalidad = TypesUtil.convertListToMap("codigo", modalidades);

        if (configuraEncuesta.getSimultaneo() == 1) {
            Map<Long, List<Alumno>> mapAlumnos = TypesUtil.convertListToMapList("seccion.id", "matriculaResumen.alumno", matriculasSecciones);
            for (Map.Entry<Long, List<Alumno>> entry : mapAlumnos.entrySet()) {
                List<Alumno> alumnos = clearAlumnosDuplicados(entry.getValue());
                mapAlumnos.put(entry.getKey(), alumnos);
            }

            EncuestaEstudiantil encuestaEstudiantilDocente = encuestaEstudiantilDAO.findByCicloTipo(cicloAcademico, TipoExamenVirtualEnum.ENC_DOC);
            List<EncuestaDocente> listEncuestaDocente = encuestaDocenteDAO.allByEncuestaEstudiantilCiclo(encuestaEstudiantilDocente, cicloAcademico);

            visorEncuestaCurso.iniciarConteo(listEncuestaDocente.size());
            for (EncuestaDocente encuestaDocente : listEncuestaDocente) {
                Seccion seccion = encuestaDocente.getDocenteSeccion().getSeccion();
                AnexoBoletin anexoSup = seccion.getGrupoSeccion().getAnexoBoletin().getAnexoSuperior();
                ModalidadEstudioEnum modaEnum = anexoSup.isAnexoCursosPostgrado() ? ModalidadEstudioEnum.EPG : ModalidadEstudioEnum.PRE;
                ModalidadEstudio modalidad = mapModalidad.get(modaEnum.name());

                EncuestaCurso enc = new EncuestaCurso();
                enc.setEncuestaEstudiantil(encuestaTipoCurso);
                enc.setGrupoSeccion(encuestaDocente.getDocenteSeccion().getSeccion().getGrupoSeccion());
                enc.setModalidadEstudio(modalidad);
                enc.setEncuestaDocente(encuestaDocente);

                enc.setAlumnosFin(encuestaDocente.getAlumnosFin());
                enc.setAlumnosInicio(encuestaDocente.getAlumnosInicio());
                enc.setAlumnosEncuestados(0L);
                enc.setEstadoEnum(encuestaDocente.getEstadoEnum());
                enc.setFechaEncuestaInicio(encuestaDocente.getFechaEncuestaInicio());
                enc.setFechaEncuestaFin(encuestaDocente.getFechaEncuestaFin());
                enc.setUserRegistro(ds.getUsuario());
                enc.setFechaRegistro(new Date());
                enc.setDescripcion(encuestaDocente.getDescripcion());
                encuestaCursoDAO.save(enc);

                visorEncuestaCurso.incrementar();
                if (encuestaDocente.getEstadoEnum() == ACT) {

                    List<Alumno> alumnos = TypesUtil.getListNotNull(mapAlumnos.get(seccion.getId()));
                    saveEncuestaAlumno(enc, encuestaDocente, alumnos, encuestasAlumnos, ds);

                    encuestaTipoCurso.setObjetivosEncuesta(encuestaTipoCurso.getObjetivosEncuesta() + 1);
                    encuestaTipoCurso.setEncuestasProgramadas(encuestaTipoCurso.getEncuestasProgramadas() + alumnos.size());
                }

            }

        } else {
            Map<Long, List<Alumno>> mapAlumnos = TypesUtil.convertListToMapList("seccion.grupoSeccion.id", "matriculaResumen.alumno", matriculasSecciones);
            for (Map.Entry<Long, List<Alumno>> entry : mapAlumnos.entrySet()) {
                List<Alumno> alumnos = clearAlumnosDuplicados(entry.getValue());
                mapAlumnos.put(entry.getKey(), alumnos);
            }

            List<PeriodoEncuesta> periodosEncuesta = periodoEncuestaDAO.allByEncuesta(encuestaTipoCurso);

            List<CursoSinEncuesta> cursosSinEncuesta = cursoSinEncuestaDAO.allByEncuestaEstudiantil(encuestaTipoCurso);
            Map<Long, Curso> mapCursosSinEncuesta = TypesUtil.convertListToMap("curso.id", "curso", cursosSinEncuesta);
            visorEncuestaCurso.iniciarConteo(mapGposSeccion.size());

            for (GrupoSeccion grupoSeccion : mapGposSeccion.values()) {
                saveEncuestaCurso(
                        grupoSeccion,
                        mapAlumnos,
                        mapCursosSinEncuesta,
                        configuraEncuesta,
                        periodosEncuesta,
                        encuestaTipoCurso,
                        encuestasAlumnos,
                        mapModalidad, ds);
                visorEncuestaCurso.incrementar();
            }
        }

        saveEncuestaAlumno(null, null, new ArrayList(), encuestasAlumnos, ds);
        encuestaEstudiantilDAO.update(encuestaTipoCurso);

    }

    private void saveEncuestaCurso(
            GrupoSeccion grupoSeccion,
            Map<Long, List<Alumno>> mapAlumnos,
            Map<Long, Curso> mapCursosSinEncuesta,
            ConfiguraEncuesta configuraEncuesta,
            List<PeriodoEncuesta> periodosEncuesta,
            EncuestaEstudiantil encuestaEstudiantil,
            List<EncuestaAlumno> encuestasAlumnos,
            Map<String, ModalidadEstudio> mapModalidad,
            DataSessionPivot ds) {

        List<Alumno> alumnos = TypesUtil.getListNotNull(mapAlumnos.get(grupoSeccion.getId()));
        long cantidad = Long.valueOf(alumnos.size());

        AnexoBoletin anexoSup = grupoSeccion.getAnexoBoletin().getAnexoSuperior();
        ModalidadEstudioEnum modaEnum = anexoSup.isAnexoCursosPostgrado() ? ModalidadEstudioEnum.EPG : ModalidadEstudioEnum.PRE;
        ModalidadEstudio modalidad = mapModalidad.get(modaEnum.name());

        String impedido = null;

        if (alumnos.size() < configuraEncuesta.getCantidadMinimaAlumnosPregrado()) {
            impedido = (impedido == null ? "" : impedido);
            impedido += "Anulada porque no tiene la cantidad mínima de alumnos. ";
        }

        Curso curso = grupoSeccion.getCurso();
        Curso cursoNoEnc = mapCursosSinEncuesta.get(curso.getId());
        if (cursoNoEnc != null) {
            impedido = (impedido == null ? "" : impedido);
            impedido += "Anulada porque este curso está configurado para no ser encuestado. ";
        }

        if (impedido != null) {
            EncuestaCurso enc = new EncuestaCurso();
            enc.setEncuestaEstudiantil(encuestaEstudiantil);
            enc.setGrupoSeccion(grupoSeccion);
            enc.setModalidadEstudio(modalidad);

            enc.setAlumnosEncuestados(0L);
            enc.setAlumnosInicio(cantidad);
            enc.setAlumnosFin(cantidad);
            enc.setDescripcion(impedido);
            enc.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ANU);
            enc.setUserRegistro(ds.getUsuario());
            enc.setFechaRegistro(new Date());
            encuestaCursoDAO.save(enc);
            return;
        }

        for (PeriodoEncuesta periodoEncuesta : periodosEncuesta) {
            EncuestaCurso enc = new EncuestaCurso();
            enc.setEncuestaEstudiantil(encuestaEstudiantil);
            enc.setGrupoSeccion(grupoSeccion);
            enc.setModalidadEstudio(modalidad);

            enc.setAlumnosFin(cantidad);
            enc.setAlumnosInicio(cantidad);
            enc.setAlumnosEncuestados(0L);
            enc.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
            enc.setFechaEncuestaInicio(periodoEncuesta.getFechaInicio());
            enc.setFechaEncuestaFin(periodoEncuesta.getFechaFin());
            enc.setUserRegistro(ds.getUsuario());
            enc.setFechaRegistro(new Date());
            encuestaCursoDAO.save(enc);
            saveEncuestaAlumno(enc, null, alumnos, encuestasAlumnos, ds);

            encuestaEstudiantil.setEncuestasProgramadas(encuestaEstudiantil.getEncuestasProgramadas() + alumnos.size());
            encuestaEstudiantil.setObjetivosEncuesta(encuestaEstudiantil.getObjetivosEncuesta() + 1);
        }
    }

    private void saveEncuestaAlumno(
            EncuestaCurso encuestaCurso,
            EncuestaDocente encuestaDocente,
            List<Alumno> alumnos,
            List<EncuestaAlumno> encuestasAlumnos,
            DataSessionPivot ds) {

        for (Alumno alumno : alumnos) {
            EncuestaAlumno encuAlumno = new EncuestaAlumno();
            encuAlumno.setAlumno(alumno);
            encuAlumno.setEncuestaCurso(encuestaCurso);
            encuAlumno.setEncuestaDocente(encuestaDocente);
            if (encuestaCurso.getEstadoEnum() == EncuestaEstudiantilEstadoEnum.ACT) {
                encuAlumno.setEstadoEnum(EncuestaEstudiantilEstadoEnum.PEND);
            } else {
                encuAlumno.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ANU);
            }
            encuAlumno.setUserRegistro(ds.getUsuario());
            encuAlumno.setFechaRegistro(new Date());
            //encuestaAlumnoDAO.save(encuAlumno);
            encuestasAlumnos.add(encuAlumno);
        }

        if (encuestasAlumnos.size() > 2000) {
            encuestaAlumnoDAO.saveList(encuestasAlumnos);
            encuestasAlumnos.clear();
        }

        if (encuestaCurso == null) {
            encuestaAlumnoDAO.saveList(encuestasAlumnos);
            encuestasAlumnos.clear();
        }
    }

    private List<Alumno> clearAlumnosDuplicados(List<Alumno> alumnosDobles) {
        Map<Long, Alumno> mapAlumnos = TypesUtil.convertListToMap("id", alumnosDobles);
        return new ArrayList(mapAlumnos.values());
    }

}
