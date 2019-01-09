package pe.edu.lamolina.pivot.controller.academico.avancecurricular;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.CursoEquivalente;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.RequisitoCursoCurricula;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoSimultaneoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoEquivalenteDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCurricularDAO;
import pe.edu.lamolina.pivot.dao.academico.RequisitoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class AvanceCurricularServiceImp implements AvanceCurricularService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PlanCurricularDAO planCurricularDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;

    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;

    @Autowired
    RequisitoCursoCurriculaDAO requisitoCursoCurriculaDAO;

    @Autowired
    CursoCurriculaDAO cursoCurriculaDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    AlumnoCursoSimultaneoDAO alumnoCursoSimultaneoDAO;

    @Autowired
    AvanceCurricularAsincronoService avanceCurricularAsincronoService;

    @Autowired
    CursoEquivalenteDAO cursoEquivalenteDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Override
    @Transactional
    public void generarAvanceCurricularByPlanCurricular(PlanCurricular planCurricular, DataSessionPivot ds) {
        PlanCurricular planBD = planCurricularDAO.find(planCurricular.getId());
        List<Alumno> alumnos = alumnoDAO.allByPlanCurricular(planBD);

        Map<Long, CursoCurricula> mapCursoCurricula = new HashMap<>();
        Map<Long, List<RequisitoCursoCurricula>> mapRequisitoCursoCurricula = new HashMap<>();
        Map<Long, List<CursoEquivalente>> mapCursosEquivalentes = new HashMap<>();

        obtenerData(planBD, mapCursoCurricula, mapRequisitoCursoCurricula, mapCursosEquivalentes);

        logger.debug("Cantidad de alumnos: {}", alumnos.size());
        logger.debug("Cantidad de Cursos: {}", mapCursoCurricula.size());

        List<MatriculaCurso> cursosMatriculados = matriculaCursoDAO.allActivoByAlumnosCicloActivo(alumnos);
        Map<Long, List<MatriculaCurso>> mapCursosMatriculados = TypesUtil.convertListToMapList("matriculaResumen.alumno.id", cursosMatriculados);

        List<AlumnoCicloCurso> cursosAprobados = alumnoCicloCursoDAO.allAprobadoActivoByAlumnos(alumnos);
        Map<Long, List<AlumnoCicloCurso>> mapCursosAprobados = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", cursosAprobados);
        
        List<AlumnoCicloCurso> cursosVecesLlevado = alumnoCicloCursoDAO.allVecesAprobadoByAlumnos(alumnos);
        Map<String, AlumnoCicloCurso> mapCursosVecesLlevado = TypesUtil.convertListToMapList("alumnoCursoKey", cursosVecesLlevado);
        for (AlumnoCicloCurso cursoAprobado : cursosAprobados) {
            cursoAprobado.setVecesCursadoTransient(0);
            AlumnoCicloCurso cursoVeces = mapCursosVecesLlevado.get(cursoAprobado.getAlumnoCursoKey());
            if (cursoVeces == null) {
                continue;
            }
            cursoAprobado.setVecesCursadoTransient(cursoVeces.getVecesCursado());
        }

        for (Alumno alumno : alumnos) {
            avanceCurricularAsincronoService.deleteAllAlumnoCursoSimultaneoByAlumno(alumno);
        }

        for (Alumno alumno : alumnos) {
            List<MatriculaCurso> cursosMatriculadosAlumno = mapCursosMatriculados.get(alumno.getId());
            List<AlumnoCicloCurso> cursosAprobadosAlumno = mapCursosAprobados.get(alumno.getId());
            avanceCurricularAsincronoService.procesarAlumno(alumno, mapCursoCurricula, mapRequisitoCursoCurricula, mapCursosEquivalentes, cursosMatriculadosAlumno, cursosAprobadosAlumno, ds);
        }
    }

    private void obtenerData(
            PlanCurricular planCurricular,
            Map<Long, CursoCurricula> mapCursoCurricula,
            Map<Long, List<RequisitoCursoCurricula>> mapRequisitoCursoCurricula,
            Map<Long, List<CursoEquivalente>> mapCursosEquivalentes) {

        List<CursoCurricula> cursos = cursoCurriculaDAO.allByPlanCurricular(planCurricular);
        for (CursoCurricula curso : cursos) {
            if (curso.getNumeroCiclo() == 0) {
                continue;
            }
            mapCursoCurricula.put(curso.getId(), curso);
        }

        List<RequisitoCursoCurricula> requisitoCursoCurriculas = requisitoCursoCurriculaDAO.allByPlanCurricular(planCurricular);
        for (RequisitoCursoCurricula rcc : requisitoCursoCurriculas) {
            Long key = rcc.getCursoCurricula().getId();
            List<RequisitoCursoCurricula> lista = mapRequisitoCursoCurricula.get(key);
            if (lista == null) {
                lista = new ArrayList<>();
                mapRequisitoCursoCurricula.put(key, lista);
            }
            lista.add(rcc);
        }

        List<CursoEquivalente> cursoEquivalentes = cursoEquivalenteDAO.allActivoByPlanCurricular(planCurricular);
        for (CursoEquivalente ce : cursoEquivalentes) {
            Long key = ce.getCursoCurricula().getId();
            List<CursoEquivalente> lista = mapCursosEquivalentes.get(key);
            if (lista == null) {
                lista = new ArrayList<>();
                mapCursosEquivalentes.put(key, lista);
            }
            lista.add(ce);
        }

    }

    @Override
    @Transactional
    public void generarAvanceCurricularByAlumno(Alumno alumno, DataSessionPivot ds) {

        Alumno alumnoBD = alumnoDAO.findAllInfo(alumno.getId());
        Map<Long, CursoCurricula> mapCursoCurricula = new HashMap<>();
        Map<Long, List<RequisitoCursoCurricula>> mapRequisitoCursoCurricula = new HashMap<>();
        Map<Long, List<CursoEquivalente>> mapCursosEquivalentes = new HashMap<>();
        obtenerData(alumnoBD.getPlanCurricular(), mapCursoCurricula, mapRequisitoCursoCurricula, mapCursosEquivalentes);

        List<MatriculaCurso> cursosMatriculados = matriculaCursoDAO.allActivoByAlumnoCicloActivo(alumno);
        List<AlumnoCicloCurso> cursosAprobados = alumnoCicloCursoDAO.allAprobadoActivoByAlumno(alumno);

        alumnoCursoSimultaneoDAO.deleteAllByAlumno(alumnoBD);
        avanceCurricularAsincronoService.procesarAlumnoSincrono(alumnoBD, mapCursoCurricula, mapRequisitoCursoCurricula, mapCursosEquivalentes, cursosMatriculados, cursosAprobados, ds);
    }

    @Override
    @Transactional
    public void desvincularCursoCurricula(PlanCurricular plan, DataSessionPivot ds) {
        PlanCurricular planBD = planCurricularDAO.find(plan.getId());
        List<Alumno> alumnos = alumnoDAO.allByPlanCurricular(planBD);

        for (Alumno alumno : alumnos) {
            avanceCurricularAsincronoService.deleteAllAlumnoCursoSimultaneoByAlumno(alumno);
            avanceCurricularAsincronoService.deleteAllAlumnoCursoCurriculaByAlumno(alumno);

            alumno.setPlanCurricular(null);
            alumnoDAO.update(alumno);
        }
    }

}
