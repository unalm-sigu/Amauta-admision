package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.curso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.encuestaestudiantil.ConfiguraEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.CursoSinEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaAlumno;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaCurso;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.encuestaestudiantil.PeriodoEncuesta;
import pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.encuesta.ConfiguraEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.CursoSinEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaAlumnoDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaCursoDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaDocenteDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaEstudiantilDAO;
import pe.edu.lamolina.pivot.dao.encuesta.PeriodoEncuestaDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

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
        Map<Long, List<Alumno>> mapAlumnos = TypesUtil.convertListToMapList("seccion.grupoSeccion.id", "matriculaResumen.alumno", matriculasSecciones);
        for (Map.Entry<Long, List<Alumno>> entry : mapAlumnos.entrySet()) {
            List<Alumno> alumnos = clearAlumnosDuplicados(entry.getValue());
            mapAlumnos.put(entry.getKey(), alumnos);
        }

        visorEncuestaCurso.setEstado("Obteniendo información de cursos-secciones");

        Map<Long, GrupoSeccion> mapGposSeccion = TypesUtil.convertListToMap("seccion.grupoSeccion.id", "seccion.grupoSeccion", matriculasSecciones);

        EncuestaEstudiantil encuestaEstudiantil = encuestaEstudiantilDAO.findByCicloTipo(cicloAcademico, TipoExamenVirtualEnum.ENC_CUR);
        ConfiguraEncuesta configuraEncuesta = configuraEncuestaDAO.findByEncuesta(encuestaEstudiantil);

        if (configuraEncuesta.getSimultaneo() == 1) {
            EncuestaEstudiantil encuestaEstudiantilDocente = encuestaEstudiantilDAO.findByCicloTipo(cicloAcademico, TipoExamenVirtualEnum.ENC_DOC);
            List<EncuestaDocente> listEncuestaDocente = encuestaDocenteDAO.allByEncuestaEstudiantilCiclo(encuestaEstudiantilDocente, cicloAcademico);
            List<EncuestaAlumno> listEncuestaAlumno = encuestaAlumnoDAO.allByListEncuestaDocente(listEncuestaDocente);
            Map<Long, List<EncuestaAlumno>> maplistAlm = TypesUtil.convertListToMapList("encuestaDocente.id", listEncuestaAlumno);

            visorEncuestaCurso.iniciarConteo(listEncuestaDocente.size());

            for (EncuestaDocente encuestaDocente : listEncuestaDocente) {
                listEncuestaAlumno = TypesUtil.getListNotNull(maplistAlm.get(encuestaDocente.getId()));
                List<Alumno> listAlumno = listEncuestaAlumno.stream().map(EncuestaAlumno::getAlumno).collect(Collectors.toList());
                EncuestaCurso enc = new EncuestaCurso();
                enc.setAlumnosFin(encuestaDocente.getAlumnosFin());
                enc.setAlumnosInicio(encuestaDocente.getAlumnosInicio());
                enc.setAlumnosEncuestados(0L);
                enc.setEstadoEnum(encuestaDocente.getEstadoEnum());
                enc.setEncuestaEstudiantil(encuestaEstudiantil);
                enc.setFechaEncuestaInicio(encuestaDocente.getFechaEncuestaInicio());
                enc.setFechaEncuestaFin(encuestaDocente.getFechaEncuestaFin());
                enc.setGrupoSeccion(encuestaDocente.getDocenteSeccion().getSeccion().getGrupoSeccion());
                enc.setUserRegistro(ds.getUsuario());
                enc.setFechaRegistro(new Date());
                encuestaCursoDAO.save(enc);
                saveEncuestaAlumno(enc, listAlumno, ds);
                visorEncuestaCurso.incrementar();
            }
        } else {
            List<PeriodoEncuesta> periodosEncuesta = periodoEncuestaDAO.allByEncuesta(encuestaEstudiantil);

            List<CursoSinEncuesta> cursosSinEncuesta = cursoSinEncuestaDAO.allByEncuestaEstudiantil(encuestaEstudiantil);
            Map<Long, Curso> mapCursosSinEncuesta = TypesUtil.convertListToMap("curso.id", "curso", cursosSinEncuesta);
            visorEncuestaCurso.iniciarConteo(mapGposSeccion.size());

            for (GrupoSeccion grupoSeccion : mapGposSeccion.values()) {
                saveEncuestaCurso(
                        grupoSeccion,
                        mapAlumnos,
                        mapCursosSinEncuesta,
                        configuraEncuesta,
                        periodosEncuesta,
                        encuestaEstudiantil, ds);
                visorEncuestaCurso.incrementar();
            }
        }
        encuestaEstudiantilDAO.update(encuestaEstudiantil);

    }

    private void saveEncuestaCurso(
            GrupoSeccion grupoSeccion,
            Map<Long, List<Alumno>> mapAlumnos,
            Map<Long, Curso> mapCursosSinEncuesta,
            ConfiguraEncuesta configuraEncuesta,
            List<PeriodoEncuesta> periodosEncuesta,
            EncuestaEstudiantil encuestaEstudiantil,
            DataSessionPivot ds) {

        List<Alumno> alumnos = mapAlumnos.get(grupoSeccion.getId());
        alumnos = (alumnos == null) ? new ArrayList() : alumnos;

        String impedido = null;

        if (alumnos.size() < configuraEncuesta.getCantidadMinimaAlumnos()) {
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
            enc.setAlumnosEncuestados(0L);
            enc.setAlumnosInicio(Long.valueOf(alumnos.size()));
            enc.setAlumnosFin(Long.valueOf(alumnos.size()));
            enc.setDescripcion(impedido);
            enc.setGrupoSeccion(grupoSeccion);
            enc.setEncuestaEstudiantil(encuestaEstudiantil);
            enc.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ANU);
            enc.setUserRegistro(ds.getUsuario());
            enc.setFechaRegistro(new Date());
            encuestaCursoDAO.save(enc);
            return;
        }

        for (PeriodoEncuesta periodoEncuesta : periodosEncuesta) {
            EncuestaCurso enc = new EncuestaCurso();
            enc.setAlumnosFin((long) alumnos.size());
            enc.setAlumnosInicio((long) alumnos.size());
            enc.setAlumnosEncuestados(0L);
            enc.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
            enc.setEncuestaEstudiantil(encuestaEstudiantil);
            enc.setFechaEncuestaInicio(periodoEncuesta.getFechaInicio());
            enc.setFechaEncuestaFin(periodoEncuesta.getFechaFin());
            enc.setGrupoSeccion(grupoSeccion);
            enc.setUserRegistro(ds.getUsuario());
            enc.setFechaRegistro(new Date());
            encuestaCursoDAO.save(enc);
            saveEncuestaAlumno(enc, alumnos, ds);
        }

        encuestaEstudiantil.setObjetivosEncuesta(encuestaEstudiantil.getObjetivosEncuesta() + 1);
        encuestaEstudiantil.setEncuestasProgramadas(encuestaEstudiantil.getEncuestasProgramadas() + alumnos.size());
    }

    private void saveEncuestaAlumno(EncuestaCurso encuestaCurso, List<Alumno> alumnos, DataSessionPivot ds) {

        for (Alumno alumno : alumnos) {
            EncuestaAlumno encuAlumno = new EncuestaAlumno();
            encuAlumno.setAlumno(alumno);
            encuAlumno.setEncuestaCurso(encuestaCurso);
            if (encuestaCurso.getEstadoEnum() == EncuestaEstudiantilEstadoEnum.ACT) {
                encuAlumno.setEstadoEnum(EncuestaEstudiantilEstadoEnum.PEND);
            } else {
                encuAlumno.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ANU);
            }
            encuAlumno.setUserRegistro(ds.getUsuario());
            encuAlumno.setFechaRegistro(new Date());
            encuestaAlumnoDAO.save(encuAlumno);
        }
    }

    private List<Alumno> clearAlumnosDuplicados(List<Alumno> alumnosDobles) {
        Map<Long, Alumno> mapAlumnos = TypesUtil.convertListToMap("id", alumnosDobles);
        return new ArrayList(mapAlumnos.values());
    }

}
