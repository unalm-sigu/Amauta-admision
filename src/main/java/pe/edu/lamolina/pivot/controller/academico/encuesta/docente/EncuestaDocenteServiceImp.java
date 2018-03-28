package pe.edu.lamolina.pivot.controller.academico.encuesta.docente;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.util.ListHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.encuesta.ConfiguraEncuesta;
import pe.edu.lamolina.model.encuesta.CursoSinEncuesta;
import pe.edu.lamolina.model.encuesta.EncuestaAlumno;
import pe.edu.lamolina.model.encuesta.EncuestaDocente;
import pe.edu.lamolina.model.encuesta.EncuestaEstudiantil;
import pe.edu.lamolina.model.enums.EncuestaAlumnoEstadoEnum;
import pe.edu.lamolina.model.enums.EncuestaDocenteEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.encuesta.ConfiguraEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.CursoSinEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaAlumnoDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaDocenteDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaEstudiantilDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class EncuestaDocenteServiceImp implements EncuestaDocenteService {

    @Autowired
    EncuestaDocenteDAO encuestaDocenteDAO;
    @Autowired
    DocenteDAO docenteDAO;
    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;
    @Autowired
    EncuestaEstudiantilDAO encuestaEstudiantilDAO;
    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;
    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;
    @Autowired
    ConfiguraEncuestaDAO configuraEncuestaDAO;
    @Autowired
    CursoSinEncuestaDAO cursoSinEncuestaDAO;
    @Autowired
    EncuestaAlumnoDAO encuestaAlumnoDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<EncuestaDocente> allEncuestaDocente(DynatableFilter filter, CicloAcademico ciclo) {
        return encuestaDocenteDAO.allByDynatable(filter, ciclo);
    }

    @Override
    @Transactional
    public void generarEncuesta(DataSessionPivot ds) {
        logger.debug("generar encuesta");
        long DAYSINMS = 1000 * 60 * 60 * 24;

        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        List<MatriculaSeccion> matriculaSeccions = matriculaSeccionDAO.allByCiclo(cicloAcademico);

        logger.debug("matriculaSeccions {}", matriculaSeccions.size());
        Map<Long, List<MatriculaSeccion>> matriculaSeccionMap = TypesUtil.convertListToMapList("seccion.id", matriculaSeccions);

        List<DocenteSeccion> docenteSeccions = docenteSeccionDAO.allDocenteSeccionByModalidad(cicloAcademico, modalidad);

        Map<Long, List<DocenteSeccion>> docenteSeccionMap = TypesUtil.convertListToMapList("seccion.id", docenteSeccions);
        EncuestaEstudiantil encuestaEstudiantil = encuestaEstudiantilDAO.allByCicloTipo(cicloAcademico, modalidad, TipoExamenVirtualEnum.ENC_DOC);
        if (encuestaEstudiantil == null) {
            throw new PhobosException("No existe ninguna encuesta activa");
        }
        ConfiguraEncuesta configuraEncuesta = configuraEncuestaDAO.findConfiguraEncuestaByEncuestaEstudiantil(encuestaEstudiantil);
        if (configuraEncuesta == null) {
            throw new PhobosException("No esta configurada la encuesta activa");
        }

        Long cantidadMaximaDocentes = configuraEncuesta.getCantidadMaximaDocentes();
        Long cantidadMinimaAlumnos = configuraEncuesta.getCantidadMinimaAlumnos();
        logger.debug("cantidadMaximaDocentes {} cantidadMinimaAlumnos {}", cantidadMaximaDocentes, cantidadMinimaAlumnos);

        List<CursoSinEncuesta> cursosSinEncuesta = cursoSinEncuestaDAO.allByEncuestaEstudiantil(encuestaEstudiantil);
        Map<Long, Curso> cursoSinEncuestaMap = TypesUtil.convertListToMap("curso.id", "curso", cursosSinEncuesta);

        List<EncuestaDocente> encuestaDocentes = encuestaDocenteDAO.allByEncuestaEstudiantil(encuestaEstudiantil);
        Map<Long, EncuestaDocente> encuestaDocenteSeccionMap = TypesUtil.convertListToMap("docenteSeccion.id", encuestaDocentes);
        Map<Long, Map<Long, Seccion>> grupoSeccionMap = new LinkedHashMap();

        for (DocenteSeccion docenteSeccion : docenteSeccions) {
            Seccion seccion = docenteSeccion.getSeccion();
            GrupoSeccion grupo = seccion.getGrupoSeccion();
            Map<Long, Seccion> seccionMap = grupoSeccionMap.get(grupo.getId());
            if (seccionMap == null) {
                seccionMap = new ListHashMap<>();
            }
            seccionMap.put(grupo.getId(), seccion);
            grupoSeccionMap.put(grupo.getId(), seccionMap);
        }

        for (DocenteSeccion docenteSeccion : docenteSeccions) {

            EncuestaDocente sd = encuestaDocenteSeccionMap.get(docenteSeccion.getId());
            if (sd != null) {
                continue;
            }

            Docente docente = docenteSeccion.getDocente();
            Seccion seccion = docenteSeccion.getSeccion();
            GrupoSeccion grupo = seccion.getGrupoSeccion();
            Curso curso = grupo.getCurso();

            Curso cursoSinEncuesta = cursoSinEncuestaMap.get(curso.getId());
            if (cursoSinEncuesta != null) {
                continue;
            }

            EncuestaDocente encuestaDocente = new EncuestaDocente();
            encuestaDocente.setEstadoEnum(EncuestaDocenteEstadoEnum.ACT);

            Map<Long, Seccion> seccionMap = grupoSeccionMap.get(grupo.getId());
            if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {
                Docente docenteTeoria = this.getDocenteTeoria(seccionMap, docenteSeccionMap);
                //logger.debug("grupo {} TipoSeccion {} docente {} docente teoria {} ", grupo.getId(), seccion.getTipoSeccion(), docente.getId(), docenteTeoria.getId());
                if (docenteTeoria.getId() == docente.getId().longValue()) {
                    encuestaDocente.setEstadoEnum(EncuestaDocenteEstadoEnum.TEO);
                }
            }
            Date fechaFinSeccion = docenteSeccion.getFechaFin();
            if (fechaFinSeccion != null) {
                Date fechaInicio = new Date(fechaFinSeccion.getTime() - 14 * DAYSINMS);
                Date fechaFin = new Date(fechaFinSeccion.getTime() - 7 * DAYSINMS);
                encuestaDocente.setFechaInicio(fechaInicio);
                encuestaDocente.setFechaFin(fechaFin);
            }

            encuestaDocente.setDocenteSeccion(docenteSeccion);
            encuestaDocente.setEncuestaEstudiantil(encuestaEstudiantil);
            encuestaDocente.setAlumnoFin(0L);
            encuestaDocente.setAlumnosInicio(0L);
            encuestaDocente.setAlumnosEncuestados(0L);
            encuestaDocente.setEsTeoriaPractica(0);
            encuestaDocente.setFechaEncuesta(new Date());
            encuestaDocenteDAO.save(encuestaDocente);

            encuestaDocenteSeccionMap.put(docenteSeccion.getId(), encuestaDocente);
            List<EncuestaAlumno> encuestados = new ArrayList();
            if (encuestaDocente.getEstadoEnum() != EncuestaDocenteEstadoEnum.TEO) {
                this.makeEncuestaAlumno(matriculaSeccionMap, encuestaDocente, seccion, ds, encuestados);
                if (encuestados.size() < cantidadMinimaAlumnos) {
                    logger.debug(" encuestados {} cantidadMinimaAlumnos {} ", encuestados.size(), cantidadMinimaAlumnos);
                    encuestaDocente.setEstadoEnum(EncuestaDocenteEstadoEnum.INA);
                    encuestaDocenteDAO.update(encuestaDocente);
                }
            }
        }
    }

    private Seccion getSeccionTeoria(Map<Long, Seccion> seccionMap) {
        for (Seccion seccion : seccionMap.values()) {
            if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.TCUR) {
                return seccion;
            }
        }
        return null;
    }

    private Docente getDocenteTeoria(Map<Long, Seccion> seccionMap, Map<Long, List<DocenteSeccion>> docenteSeccionMap) {
        if (seccionMap == null) {
            return new Docente(0);
        }
        Seccion seccionTeoria = this.getSeccionTeoria(seccionMap);
        if (seccionTeoria == null) {
            return new Docente(0);
        }
        List<DocenteSeccion> docs = docenteSeccionMap.get(seccionTeoria.getId());
        for (DocenteSeccion doc : docs) {
            if (doc.getSeccion().getTipoSeccionEnum() == TipoSeccionEnum.TCUR) {
                return doc.getDocente();
            }
        }
        return new Docente(0);
    }

    private void makeEncuestaAlumno(Map<Long, List<MatriculaSeccion>> matriculaSeccionMap, EncuestaDocente encuestaDocente, Seccion seccion, DataSessionPivot ds, List<EncuestaAlumno> encuestados) {

        List<MatriculaSeccion> matriculaSeccion = matriculaSeccionMap.get(seccion.getId());

        if (matriculaSeccion == null) {
            return;
        }

        for (MatriculaSeccion ms : matriculaSeccion) {
            EncuestaAlumno encuesta = new EncuestaAlumno();
            MatriculaResumen mr = ms.getMatriculaResumen();
            Alumno alumno = mr.getAlumno();
            encuesta.setAlumno(alumno);
            encuesta.setEncuestaDocente(encuestaDocente);
            encuesta.setEstadoEnum(EncuestaAlumnoEstadoEnum.ACT);
            encuesta.setUserRegistro(ds.getUsuario());
            encuesta.setFechaRegistro(new Date());
            encuestaAlumnoDAO.save(encuesta);
            encuestados.add(encuesta);
        }

    }
}
