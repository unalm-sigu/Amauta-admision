package pe.edu.lamolina.pivot.controller.docente.ampliacionvacante;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.PlanCalificacionCurso;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCalificacionCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.zelper.misc.MapUtil;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class AmpliacionVacanteServiceImp implements AmpliacionVacanteService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    PlanCalificacionCursoDAO planCalificacionCursoDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Override
    public List<GrupoSeccion> allGrupoByDocente(Docente docente, CicloAcademico ciclo, DataSessionPivot ds) {

        List<DocenteSeccion> docentesSecciones = docenteSeccionDAO.allByDocente(docente, ciclo);
        Map<Long, DocenteSeccion> mapDocentesSeccion = MapUtil.storeItems("seccion.id", docentesSecciones);

        logger.debug("Cantidad docente seccion {}", docentesSecciones.size());
        List<Long> idsGpoSecc = new ArrayList();
        for (DocenteSeccion docenteSeccion : docentesSecciones) {
            idsGpoSecc.add(docenteSeccion.getSeccion().getGrupoSeccion().getId());
            logger.debug("la seccion {}, grupo {}", docenteSeccion.getSeccion().getId(), docenteSeccion.getSeccion().getGrupoSeccion().getId());
        }

        logger.debug("Lista de grupos para el filtro {}", StringUtils.join(idsGpoSecc, ","));
        List<GrupoSeccion> gruposSeccion = grupoSeccionDAO.allByFilter(idsGpoSecc, ciclo, null, EstadoEnum.ACT);
        logger.debug("Lista grupo seccion tamaño {}", gruposSeccion.size());
        List<DocenteSeccion> responsables = docenteSeccionDAO.allResponsablesByGpoSecciones(gruposSeccion, ciclo);

        Map<Long, DocenteSeccion> mapResponsables = MapUtil.storeItems("seccion.grupoSeccion.id", responsables);
        for (GrupoSeccion grupoSeccion : gruposSeccion) {
            grupoSeccion.setSecciones(new ArrayList());
            DocenteSeccion responsable = mapResponsables.get(grupoSeccion.getId());
            grupoSeccion.setDocenteResponsable(responsable.getDocente());
        }

        Map<Long, GrupoSeccion> mapGposSeccion = MapUtil.storeItems("id", gruposSeccion);

        List<Seccion> secciones = seccionDAO.allActivosByGposSeccion(gruposSeccion);
        Map<Long, Seccion> mapSecciones = MapUtil.storeItems("id", secciones);
        for (Seccion seccion : secciones) {
            seccion.setDocenteSeccion(new ArrayList());
            GrupoSeccion gpoSecc = mapGposSeccion.get(seccion.getGrupoSeccion().getId());
            seccion.setGrupoSeccion(gpoSecc);
            gpoSecc.getSecciones().add(seccion);

            DocenteSeccion profeSeccion = mapDocentesSeccion.get(seccion.getId());
            Docente responsable = gpoSecc.getDocenteResponsable();
            if (profeSeccion != null) {
                seccion.setVerInformacion(true);
            } else if (responsable != null && responsable.getId() == docente.getId().longValue()) {
                seccion.setVerInformacion(true);
            }
        }

        for (DocenteSeccion profeSecc : docentesSecciones) {
            Seccion secc = mapSecciones.get(profeSecc.getSeccion().getId());
            if (secc == null) {
                continue;
            }
            profeSecc.setSeccion(secc);
            secc.getDocenteSeccion().add(profeSecc);
        }

        for (GrupoSeccion gpoSecc : gruposSeccion) {
            List<PlanCalificacionCurso> planCalificacionCursos = planCalificacionCursoDAO.allByFilter(null, ds.getCicloAcademico().getTipoEnum(), gpoSecc.getCurso(), EstadoEnum.ACT);
            gpoSecc.getCurso().setPlanesCalificacionCursos(planCalificacionCursos);
            logger.debug("PlanCalificacionCurso del curso {}, con tipo de ciclo {}, cantidad {}",
                    gpoSecc.getCurso().getId(), ds.getCicloAcademico().getTipoEnum().name(), planCalificacionCursos.size());

            List<Seccion> seccion = gpoSecc.getSecciones();
            logger.debug("GrupoSecc {}-{} tiene {} secciones", gpoSecc.getId(), gpoSecc.getCodigo(), gpoSecc.getSecciones().size());
            for (Seccion secc : seccion) {
                List<DocenteSeccion> docSeccs = secc.getDocenteSeccion();
                logger.debug("\tSeccion {}-{} hay {} docentes", secc.getCodigo(), secc.getCodigo2(), docSeccs.size());
            }
        }

        return gruposSeccion;
    }

    @Override
    public List<Alumno> allAlumnoByName(String nombre, CicloAcademico cicloAcademico) {
        return alumnoDAO.allByNameCicloAcademico(nombre, cicloAcademico);
    }

    @Override
    public void matricular(AmpliacionVacanteForm ampliacionVacanteForm, CicloAcademico cicloAcademico, DataSessionPivot ds) {

        Seccion seccionForm = ampliacionVacanteForm.getSeccion();
        Seccion seccion = seccionDAO.find(seccionForm);

        Aula aula = seccion.getAula();

        GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();
        Curso curso = grupoSeccion.getCurso();

        List<Alumno> alumnos = alumnoDAO.allByAlumnos(ampliacionVacanteForm.getAlumnos());

        for (Alumno alumno : alumnos) {

            MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, cicloAcademico);

            MatriculaCurso matriculaCurso = matriculaCursoDAO.findByMatriculaCurso(matriculaResumen, curso);
            if (matriculaCurso == null) {
                matriculaCurso = new MatriculaCurso();
                matriculaCurso.setCreditos(curso.getCreditos());
                matriculaCurso.setCreditosAprobados(0);
                matriculaCurso.setCurso(curso);
                matriculaCurso.setEstadoEnum(EstadoMatriculaEnum.MAT);
                matriculaCurso.setMatriculaResumen(matriculaResumen);
                matriculaCurso.setNotaAcumulada("0");
                matriculaCurso.setNotaAcumuladaFull("0");
                matriculaCurso.setNotaAvance("0");
                matriculaCurso.setNotaAvanceFull("0");
                matriculaCurso.setNotaFinal("0");
                matriculaCurso.setPorcentajeAvanceNota(0);
                matriculaCursoDAO.save(matriculaCurso);

            } else {
                matriculaCurso.setEstadoEnum(EstadoMatriculaEnum.MAT);
                matriculaCursoDAO.update(matriculaCurso);
            }

            MatriculaSeccion matriculaSeccion = matriculaSeccionDAO.findByMatriculaSeccion(matriculaResumen, seccion);

            if (matriculaSeccion == null) {

                matriculaSeccion = new MatriculaSeccion();
                matriculaSeccion.setCreditos(curso.getCreditos());
                matriculaSeccion.setEstadoEnum(EstadoMatriculaEnum.MAT);
                matriculaSeccion.setMatriculaResumen(matriculaResumen);
                matriculaSeccion.setSeccion(seccion);
                matriculaSeccion.setUserRegistro(ds.getUsuario());
                matriculaSeccion.setFechaRegistro(new Date());
                matriculaSeccionDAO.save(matriculaSeccion);

            } else {

                matriculaSeccion.setEstadoEnum(EstadoMatriculaEnum.MAT);
                matriculaSeccionDAO.update(matriculaSeccion);
            }

            seccion.setMatriculados(seccion.getMatriculados() + 1);
            seccion.setReservados(seccion.getReservados() - 1);
            seccion.setAmpliacionVacante(seccion.getAmpliacionVacante() + 1);
            seccionDAO.update(seccion);

            matriculaResumen.setEstadoEnum(EstadoMatriculaEnum.MAT);
            matriculaResumen.setCreditosMatriculados(matriculaResumen.getCreditosMatriculados() + curso.getCreditos());
            matriculaResumen.setCursosMatriculados(matriculaResumen.getCursosMatriculados() + 1);
            matriculaResumenDAO.update(matriculaResumen);

        }

    }

}
