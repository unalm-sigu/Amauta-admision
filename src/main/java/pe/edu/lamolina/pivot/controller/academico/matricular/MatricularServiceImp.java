package pe.edu.lamolina.pivot.controller.academico.matricular;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.matricula.MatriculaSimultaneo;
import pe.edu.lamolina.model.vacantes.VacanteAlumno;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSimultaneoDAO;
import pe.edu.lamolina.pivot.dao.academico.TurnoAtencionDAO;
import pe.edu.lamolina.pivot.dao.vacante.VacanteAlumnoDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class MatricularServiceImp implements MatricularService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TurnoAtencionDAO turnoAtencionDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    MatriculaSimultaneoDAO matriculaSimultaneoDAO;

    @Autowired
    VacanteAlumnoDAO vacanteAlumnoDAO;

    @Override
    public TurnoAtencion findTurnoAtencion(Long turnoAtencion) {
        return turnoAtencionDAO.findById(turnoAtencion);
    }

    @Override
    @Transactional
    public void matricular(TurnoAtencion turnoAtencion, DataSessionPivot ds) {

        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        List<MatriculaResumen> matriculaResumens = matriculaResumenDAO.allNoMatriculadoByCiclo(cicloAcademico);
        logger.debug("cantidad matricula resumen NMAT {}", matriculaResumens.size());

        List<MatriculaCurso> matriculaCursos = matriculaCursoDAO.allPrematriculadoByMatriculaResumen(matriculaResumens);
        Map<Long, List<MatriculaCurso>> matriculaCursosMap = TypesUtil.convertListToMapList("matriculaResumen.id", matriculaCursos);
        logger.debug("cantidad matricula curso NMAT {}", matriculaCursosMap.size());

        List<MatriculaSeccion> matriculaSeccions = matriculaSeccionDAO.allPrematriculadoByMatriculaResumen(matriculaResumens);
        Map<Long, List<MatriculaSeccion>> matriculaSeccionsMap = TypesUtil.convertListToMapList("matriculaResumen.id", matriculaSeccions);
        logger.debug("cantidad matricula seccion NMAT {}", matriculaSeccionsMap.size());
        List<Seccion> secciones = matriculaSeccions.stream().map(MatriculaSeccion::getSeccion).collect(Collectors.toList());
        List<VacanteAlumno> vacanteAlumnos = vacanteAlumnoDAO.allBySeccion(secciones);

        List<MatriculaSimultaneo> matriculaSimultaneos = matriculaSimultaneoDAO.allByMatriculaCurso(matriculaCursos);
        Map<Long, List<MatriculaSimultaneo>> matriculaSimultaneosMap = TypesUtil.convertListToMapList("matriculaCurso.id", matriculaSimultaneos);

        for (MatriculaResumen mr : matriculaResumens) {

            Alumno alumno = mr.getAlumno();
            logger.debug("alumno  {}", alumno.getId(), alumno.getCodigo());

            List<MatriculaCurso> misMatriculaCurso = matriculaCursosMap.get(mr.getId());
            Map<Long, MatriculaCurso> misMatriculaCursoMap = TypesUtil.convertListToMap("curso.id", misMatriculaCurso);
            logger.debug("cantidad de cursos a matricular  {}", misMatriculaCursoMap.size());

            List<MatriculaSeccion> misMatriculaSeccions = matriculaSeccionsMap.get(mr.getId());
            Map<Long, MatriculaSeccion> misMatriculaSeccionMap = TypesUtil.convertListToMap("seccion.id", misMatriculaSeccions);
            logger.debug("cantidad de secciones a matricular  {}", misMatriculaCurso.size());

            List<Long> pendientesMatriculaSeccion = new ArrayList();

            for (MatriculaSeccion ms : misMatriculaSeccionMap.values()) {

                Seccion seccion = ms.getSeccion();
                logger.debug("matriculando a seccion   {} {} ", seccion.getId(), seccion.getCodigo());
                logger.debug("vacantes en  seccion   {} {} ", seccion.getVacantes());
                logger.debug("reservados en  seccion   {} {} ", seccion.getReservados());
                logger.debug("matriculados en  seccion   {} {} ", seccion.getMatriculados());
                int disponibles = seccion.getVacantes() - (seccion.getReservados() + seccion.getMatriculados());
                logger.debug("disponibles en  seccion   {} {} ", disponibles);

                if (disponibles < 1) {
                    ms.setEstadoEnum(EstadoMatriculaEnum.NVAC);
                    matriculaSeccionDAO.update(ms);
                    continue;
                }

                GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();
                logger.debug("matriculando a grupo seccion   {} {} ", grupoSeccion.getId(), grupoSeccion.getCodigo());
                Curso curso = grupoSeccion.getCurso();
                logger.debug("matriculando a curso  {} {} ", curso.getId(), curso.getNombre());

                MatriculaCurso mc = misMatriculaCursoMap.get(curso.getId());

                List<MatriculaSimultaneo> misMatriculaSimultaneo = matriculaSimultaneosMap.get(mc.getId());
                if (misMatriculaSimultaneo.size() < 1) {

                    mc.setEstadoEnum(EstadoMatriculaEnum.MAT);
                    matriculaCursoDAO.update(mc);

                    mr.setCreditosMatriculados(curso.getCreditos() + mr.getCreditosMatriculados());
                    matriculaResumenDAO.update(mr);

                    ms.setEstadoEnum(EstadoMatriculaEnum.MAT);
                    matriculaSeccionDAO.update(ms);

                } else {

                    pendientesMatriculaSeccion.add(ms.getId());

                }

            }

            logger.debug("requieren simultaneo {}", pendientesMatriculaSeccion.size());

            for (Long idSeccionSimultaneo : pendientesMatriculaSeccion) {

                MatriculaSeccion ms = misMatriculaSeccionMap.get(idSeccionSimultaneo);

                Seccion seccion = ms.getSeccion();
                logger.debug("matriculando a seccion   {} {} ", seccion.getId(), seccion.getCodigo());
                logger.debug("vacantes en  seccion   {} {} ", seccion.getVacantes());
                logger.debug("reservados en  seccion   {} {} ", seccion.getReservados());
                logger.debug("matriculados en  seccion   {} {} ", seccion.getMatriculados());
                int disponibles = seccion.getVacantes() - (seccion.getReservados() + seccion.getMatriculados());
                logger.debug("disponibles en  seccion   {} {} ", disponibles);

                if (disponibles < 1) {
                    ms.setEstadoEnum(EstadoMatriculaEnum.NVAC);
                    matriculaSeccionDAO.update(ms);
                    continue;
                }

                GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();
                logger.debug("matriculando a grupo seccion   {} {} ", grupoSeccion.getId(), grupoSeccion.getCodigo());
                Curso curso = grupoSeccion.getCurso();
                logger.debug("matriculando a curso  {} {} ", curso.getId(), curso.getNombre());
                MatriculaCurso matriculaCurso = misMatriculaCursoMap.get(curso.getId());

                List<MatriculaSimultaneo> misMatriculaSimultaneo = matriculaSimultaneosMap.get(curso.getId());
                boolean cumpleSimultaneo = true;

                for (MatriculaSimultaneo matriculaSimultaneo : misMatriculaSimultaneo) {
                    MatriculaCurso matriculaCursoSimultaneo = misMatriculaCursoMap.get(matriculaSimultaneo.getMatriculaCursoSimultaneo().getId());
                    if (matriculaCursoSimultaneo.getEstadoEnum() == EstadoMatriculaEnum.NMAT) {
                        cumpleSimultaneo = false;
                    }
                }

                if (cumpleSimultaneo) {

                    matriculaCurso.setEstadoEnum(EstadoMatriculaEnum.MAT);
                    matriculaCursoDAO.update(matriculaCurso);

                    mr.setCreditosMatriculados(curso.getCreditos() + mr.getCreditosMatriculados());
                    matriculaResumenDAO.update(mr);

                    ms.setEstadoEnum(EstadoMatriculaEnum.MAT);
                    matriculaSeccionDAO.update(ms);

                } else {

                    ms.setEstadoEnum(EstadoMatriculaEnum.NMAT);
                    matriculaSeccionDAO.update(ms);

                }
            }

        }
    }

    @Override
    public Long countAllAlumnoPrematriculado(CicloAcademico cicloAcademico) {
        return matriculaCursoDAO.countAllAlumnoPrematriculado(cicloAcademico);
    }

    @Override
    public Long countAllSeccionPrematriculado(CicloAcademico cicloAcademico) {
        return matriculaSeccionDAO.countAllSeccionPrematriculado(cicloAcademico);
    }
}
