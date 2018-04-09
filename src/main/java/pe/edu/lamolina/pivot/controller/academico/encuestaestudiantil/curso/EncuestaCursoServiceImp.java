package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.curso;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.encuestaestudiantil.ConfiguraEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.CursoSinEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaAlumno;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaCurso;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.encuesta.ConfiguraEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.CursoSinEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaAlumnoDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaCursoDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaEstudiantilDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class EncuestaCursoServiceImp implements EncuestaCursoService {

    @Autowired
    EncuestaCursoDAO encuestaCursoDAO;
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
    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<EncuestaCurso> allEncuestaCurso(DynatableFilter filter, CicloAcademico ciclo) {
        return encuestaCursoDAO.allByDynatable(filter, ciclo);
    }

    @Override
    @Transactional
    public void generarEncuesta(DataSessionPivot ds) {
        logger.debug("generar encuesta");
        long DAYSINMS = 1000 * 60 * 60 * 24;

        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        logger.debug("cicloAcademico {}", cicloAcademico.getId());

        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        logger.debug("modalidad {}", modalidad.getId());
        EncuestaEstudiantil encuestaEstudiantil = encuestaEstudiantilDAO.allByCicloTipo(cicloAcademico, modalidad, TipoExamenVirtualEnum.ENC_CUR);
        if (encuestaEstudiantil == null) {
            throw new PhobosException("No existe ninguna encuesta activa");
        }
        List<MatriculaSeccion> matriculaSeccions = matriculaSeccionDAO.allByModalidadEstudioCiclo(modalidad, cicloAcademico);
        logger.debug("matriculaSeccions {}", matriculaSeccions.size());
        EventoCicloAcademico eventoCicloAcademico = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloAcademico, EventoAcademicoEnum.CLASES_PRE2);
        if (eventoCicloAcademico == null) {
            throw new PhobosException("No esta configurado el evento académico");
        }
        logger.debug("eventosCicloAcademico {}", eventoCicloAcademico.getId());

        List<CursoSinEncuesta> cursosSinEncuesta = cursoSinEncuestaDAO.allByEncuestaEstudiantil(encuestaEstudiantil);
        Map<Long, Curso> cursosSinEncuestaMap = TypesUtil.convertListToMap("curso.id", "curso", cursosSinEncuesta);

        Map<Long, GrupoSeccion> gruposSeccion = TypesUtil.convertListToMap("seccion.grupoSeccion.id", "seccion.grupoSeccion", matriculaSeccions);
        logger.debug("grupoSeccionMap {}", gruposSeccion.size());

        Map<Long, List<Alumno>> alumnoPorGrupoSeccion = TypesUtil.convertListToMapList("seccion.grupoSeccion.id", "matriculaResumen.alumno", matriculaSeccions);
        logger.debug("grupoSeccionMap {}", gruposSeccion.size());

        ConfiguraEncuesta configuraEncuesta = configuraEncuestaDAO.findConfiguraEncuestaByEncuestaEstudiantil(encuestaEstudiantil);
        if (configuraEncuesta == null) {
            throw new PhobosException("No esta configurada la encuesta activa");
        }

        Long maximoDocentes = configuraEncuesta.getCantidadMaximaDocentes();
        Long minimoAlumnos = configuraEncuesta.getCantidadMinimaAlumnos();
        logger.debug("cantidadMaximaDocentes {} cantidadMinimaAlumnos {}", maximoDocentes, minimoAlumnos);

        for (GrupoSeccion grupoSeccion : gruposSeccion.values()) {
            Curso curso = grupoSeccion.getCurso();
            logger.debug("grupoSeccion {} curso {} {} ", grupoSeccion.getId(), curso.getId(), curso.getNombre());
            Curso cursoSinEncuesta = cursosSinEncuestaMap.get(curso.getId());
            if (cursoSinEncuesta != null) {
                logger.debug("cursoSinEncuesta {} ", cursoSinEncuesta.getId());
                continue;
            }
            List<Alumno> alumnos = alumnoPorGrupoSeccion.get(grupoSeccion.getId());
            if (alumnos == null) {
                alumnos = new ArrayList();
            }
            logger.debug("cantidad alumnos {} ", alumnos.size());

            Date fechaFinEvento = eventoCicloAcademico.getFechaFin();
            Date fechaInicio = new Date(fechaFinEvento.getTime() - 15 * DAYSINMS);
            Date fechaFin = new Date(fechaFinEvento.getTime() - 7 * DAYSINMS);

            EncuestaCurso encuestaCurso = new EncuestaCurso();
            encuestaCurso.setAlumnoFin(0L);
            encuestaCurso.setAlumnosEncuestados((long) alumnos.size());
            encuestaCurso.setAlumnosInicio((long) alumnos.size());
            encuestaCurso.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
            if (minimoAlumnos > alumnos.size()) {
                encuestaCurso.setDescripcion(Constantine.REQ_MIN_ALUMNO);
                encuestaCurso.setEstadoEnum(EncuestaEstudiantilEstadoEnum.INA);
            }
            encuestaCurso.setEncuestaEstudiantil(encuestaEstudiantil);
            encuestaCurso.setFechaEncuestaInicio(fechaInicio);
            encuestaCurso.setFechaEncuestaFin(fechaFin);
            encuestaCurso.setGrupoSeccion(grupoSeccion);
            encuestaCursoDAO.save(encuestaCurso);

            for (Alumno alumno : alumnos) {
                EncuestaAlumno encuesta = new EncuestaAlumno();
                encuesta.setAlumno(alumno);
                encuesta.setEncuestaCurso(encuestaCurso);
                if (encuestaCurso.getEstadoEnum() == EncuestaEstudiantilEstadoEnum.ACT) {
                    encuesta.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
                } else {
                    encuesta.setEstadoEnum(EncuestaEstudiantilEstadoEnum.INA);
                }
                encuesta.setUserRegistro(ds.getUsuario());
                encuesta.setFechaRegistro(new Date());
                encuestaAlumnoDAO.save(encuesta);
            }

        }
    }

    @Override
    @Transactional
    public void cambiarEstadoEncuesta(EncuestaCurso encuestaForm) {
        EncuestaCurso encuesta = encuestaCursoDAO.findEncuestaCurso(encuestaForm);
        if (Strings.isNullOrEmpty(encuesta.getEstado())) {
            encuesta.setEstadoEnum(EncuestaEstudiantilEstadoEnum.INA);
        }
        if (encuesta.getEstadoEnum() == EncuestaEstudiantilEstadoEnum.ACT
                || encuesta.getEstadoEnum() == EncuestaEstudiantilEstadoEnum.TEO) {
            encuesta.setEstadoEnum(EncuestaEstudiantilEstadoEnum.INA);
            List<EncuestaAlumno> encuestas = encuestaAlumnoDAO.allByEncuestaCurso(encuesta);
            for (EncuestaAlumno encuestaAlumno : encuestas) {
                encuestaAlumno.setEstadoEnum(EncuestaEstudiantilEstadoEnum.INA);
                encuestaAlumnoDAO.update(encuestaAlumno);
            }
            return;
        }
        encuesta.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
        List<EncuestaAlumno> encuestas = encuestaAlumnoDAO.allByEncuestaCurso(encuesta);
        if (encuestas.size() < 1) {
            throw new PhobosException("No existe encuestas para el docente ");
        }
        for (EncuestaAlumno encuestaAlumno : encuestas) {
            encuestaAlumno.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
            encuestaAlumnoDAO.update(encuestaAlumno);
        }
    }
}
