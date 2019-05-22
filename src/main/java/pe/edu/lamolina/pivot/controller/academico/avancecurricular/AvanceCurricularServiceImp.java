package pe.edu.lamolina.pivot.controller.academico.avancecurricular;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.CursoEquivalente;
import pe.edu.lamolina.model.academico.CursoEquivalenteElectivo;
import pe.edu.lamolina.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.RequisitoCursoCurricula;
import pe.edu.lamolina.model.academico.ResumenPlanCurricular;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.matricula.AlumnoAvanceCurricular;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.pivot.dao.academico.AlumnoAvanceCurricularDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoSimultaneoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoEquivalenteDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoEquivalenteElectivoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoOpcionalCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCurricularDAO;
import pe.edu.lamolina.pivot.dao.academico.RequisitoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.ResumenPlanCurricularDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoCursoCurriculaDAO;
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
    CursoOpcionalCurriculaDAO cursoOpcionalCurriculaDAO;

    @Autowired
    AvanceCurricularAsincronoService avanceCurricularAsincronoService;

    @Autowired
    CursoEquivalenteDAO cursoEquivalenteDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    ResumenPlanCurricularDAO resumenPlanCurricularDAO;

    @Autowired
    TipoCursoCurriculaDAO tipoCursoCurriculaDAO;

    @Autowired
    AlumnoAvanceCurricularDAO alumnoAvanceCurricularDAO;

    @Autowired
    CursoEquivalenteElectivoDAO cursoEquivalenteElectivoDAO;

    @Override
    @Transactional
    public void generarAvanceCurricularByPlanCurricular(PlanCurricular planCurricular, DataSessionPivot ds) {
        PlanCurricular planBD = planCurricularDAO.find(planCurricular.getId());
        List<Alumno> alumnos = alumnoDAO.allByPlanCurricular(planBD);

        Map<Long, CursoCurricula> mapCursoCurricula = new HashMap<>();
        Map<Long, CursoCurricula> mapCursoCurriculaByCurso = new HashMap<>();
        Map<Long, List<RequisitoCursoCurricula>> mapRequisitoCursoCurricula = new HashMap<>();
        Map<Long, List<CursoEquivalente>> mapCursosEquivalentes = new HashMap<>();
        Map<Long, AlumnoCursoCurricula> mapAlumnoCurso = new HashMap<>();

        List<PlanCurricular> planCurriculars = planCurricularDAO.all();
        List<CursoCurricula> cursoCurriculasAll = cursoCurriculaDAO.allByPlanes(planCurriculars);
        Map<Long, List<CursoCurricula>> mapCursoCurriculaAll = TypesUtil.convertListToMapList("planCurricular.id", cursoCurriculasAll);
        obtenerData(planBD, mapCursoCurricula, mapRequisitoCursoCurricula, mapCursosEquivalentes, mapCursoCurriculaByCurso, mapCursoCurriculaAll);

        logger.debug("Cantidad de alumnos: {}", alumnos.size());
        logger.debug("Cantidad de Cursos: {}", mapCursoCurricula.size());

        List<CursoEquivalenteElectivo> cursoEquivalenteElectivos = cursoEquivalenteElectivoDAO.allCursoPlanCurricula(Arrays.asList(planCurricular));

        List<CursoOpcionalCurricula> cursoOpcionalCurriculas = cursoOpcionalCurriculaDAO.all();
        Map<Long, List<CursoOpcionalCurricula>> mapCursoOpcionalAll = TypesUtil.convertListToMapList("planCurricular.id", cursoOpcionalCurriculas);

        List<MatriculaCurso> cursosMatriculados = matriculaCursoDAO.allActivoByAlumnosCicloActivo(alumnos);
        Map<Long, List<MatriculaCurso>> mapCursosMatriculados = TypesUtil.convertListToMapList("matriculaResumen.alumno.id", cursosMatriculados);

        List<AlumnoCursoCurricula> alumnoCursoCurriculas = alumnoCursoCurriculaDAO.allByAlumnosApr(alumnos);

        List<AlumnoCicloCurso> cursosAprobados = alumnoCicloCursoDAO.allAprobadoActivoByAlumnos(alumnos);
        Map<String, AlumnoCicloCurso> mapCursosAprobados = TypesUtil.convertListToMapList("alumnoCursoKey", cursosAprobados);

        List<AlumnoCicloCurso> cursosVecesLlevado = alumnoCicloCursoDAO.allVecesLlevadoByAlumnos(alumnos);
        Map<String, AlumnoCicloCurso> mapCursosVecesLlevado = TypesUtil.convertListToMap("alumnoCursoKey", cursosVecesLlevado);

        List<TipoCursoCurricula> tipoCursoCurriculas = tipoCursoCurriculaDAO.all();

        for (AlumnoCicloCurso cursoAprobado : cursosAprobados) {
            cursoAprobado.setVecesCursadoTransient(0);
            AlumnoCicloCurso cursoVeces = mapCursosVecesLlevado.get(cursoAprobado.getAlumnoCursoKey());
            if (cursoVeces == null) {
                continue;
            }
            cursoAprobado.setVecesCursadoTransient(cursoVeces.getVecesCursado());
        }

        List<AlumnoCicloCurso> cursosDesapr = alumnoCicloCursoDAO.allDesaproActivoByAlumnos(alumnos);
        for (AlumnoCicloCurso alumnoCicloCurso : cursosDesapr) {
            if (mapCursosAprobados.get(alumnoCicloCurso.getAlumnoCursoKey()) == null) {
                cursosAprobados.add(alumnoCicloCurso);
            }
        }

        Map<Long, List<AlumnoCicloCurso>> mapCursosAlumnos = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", cursosAprobados);
        for (Alumno alumno : alumnos) {
            avanceCurricularAsincronoService.deleteAllAlumnoCursoSimultaneoByAlumno(alumno);
        }
        List<AlumnoAvanceCurricular> alumnosAvanceCurriculars = alumnoAvanceCurricularDAO.allByAlumnos(alumnos);
        List<ResumenPlanCurricular> alumnosResumenPlanCurriculars = resumenPlanCurricularDAO.all();

        for (Alumno alumno : alumnos) {
            List<AlumnoAvanceCurricular> avanceCurriculars = alumnosAvanceCurriculars.stream().filter(x -> Objects.equals(x.getAlumno().getId(), alumno.getId())).collect(Collectors.toList());

            List<ResumenPlanCurricular> resumenPlanCurriculars = alumnosResumenPlanCurriculars.stream().filter(x -> Objects.equals(x.getPlanCurricular().getId(), planBD.getId())).collect(Collectors.toList());

            List<MatriculaCurso> cursosMatriculadosAlumno = mapCursosMatriculados.get(alumno.getId());

            List<CursoOpcionalCurricula> cursoOpcional = mapCursoOpcionalAll.get(alumno.getPlanCurricular().getId());

            List<AlumnoCicloCurso> cursosAlumno = mapCursosAlumnos.get(alumno.getId());

            avanceCurricularAsincronoService.procesarAlumno(
                    alumno,
                    mapCursoCurricula,
                    mapRequisitoCursoCurricula,
                    mapCursosEquivalentes,
                    mapCursosVecesLlevado,
                    cursosMatriculadosAlumno,
                    cursosAlumno,
                    alumnoCursoCurriculas,
                    cursoOpcional,
                    mapCursoCurriculaByCurso,
                    tipoCursoCurriculas,
                    resumenPlanCurriculars,
                    avanceCurriculars,
                    cursoEquivalenteElectivos,
                    mapCursoOpcionalAll,
                    planCurriculars,
                    mapCursoCurriculaAll,
                    ds);
        }
    }

    private void obtenerData(
            PlanCurricular planCurricular,
            Map<Long, CursoCurricula> mapCursoCurricula,
            Map<Long, List<RequisitoCursoCurricula>> mapRequisitoCursoCurricula,
            Map<Long, List<CursoEquivalente>> mapCursosEquivalentes,
            Map<Long, CursoCurricula> mapCursoCurriculaByCurso,
            Map<Long, List<CursoCurricula>> mapCursoCurriculaAll
    ) {

        List<CursoCurricula> cursosCurricula = mapCursoCurriculaAll.get(planCurricular.getId());
        for (CursoCurricula cursoCurricula : cursosCurricula) {

            mapCursoCurricula.put(cursoCurricula.getId(), cursoCurricula);
            mapCursoCurriculaByCurso.put(cursoCurricula.getCurso().getId(), cursoCurricula);
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
        Map<Long, CursoCurricula> mapCursoCurriculaByCurso = new HashMap<>();
        Map<Long, AlumnoCursoCurricula> mapAlumnoCurso = new HashMap<>();
        Map<Long, List<RequisitoCursoCurricula>> mapRequisitoCursoCurricula = new HashMap<>();
        Map<Long, List<CursoEquivalente>> mapCursosEquivalentes = new HashMap<>();

        List<PlanCurricular> planCurriculars = planCurricularDAO.all();
        List<CursoCurricula> cursoCurriculasAll = cursoCurriculaDAO.allByPlanes(planCurriculars);

        Map<Long, List<CursoCurricula>> mapCursoCurriculaAll = TypesUtil.convertListToMapList("planCurricular.id", cursoCurriculasAll);
        obtenerData(alumnoBD.getPlanCurricular(), mapCursoCurricula, mapRequisitoCursoCurricula, mapCursosEquivalentes, mapCursoCurriculaByCurso, mapCursoCurriculaAll);

        List<CursoOpcionalCurricula> cursoOpcionalAllPlanes = cursoOpcionalCurriculaDAO.allByPlanCurricular(planCurriculars);
        Map<Long, List<CursoOpcionalCurricula>> mapCursoOpcionalAll = TypesUtil.convertListToMapList("planCurricular.id", cursoOpcionalAllPlanes);

        List<MatriculaCurso> cursosMatriculados = matriculaCursoDAO.allActivoByAlumnoCicloActivo(alumno);
        List<AlumnoCicloCurso> cursosAprobados = alumnoCicloCursoDAO.allAprobadoActivoByAlumno(alumno);
        List<AlumnoCicloCurso> cursosDesapr = alumnoCicloCursoDAO.allDesaproActivoByAlumno(alumno);
        for (AlumnoCicloCurso alumnoCicloCurso : cursosDesapr) {
            if (cursosAprobados.stream().filter(x -> Objects.equals(x.getCurso().getId(), alumnoCicloCurso.getCurso().getId())).findAny().orElse(null) == null) {
                cursosAprobados.add(alumnoCicloCurso);
            }
        }
        List<CursoEquivalenteElectivo> cursoEquivalenteElectivos = cursoEquivalenteElectivoDAO.allCursoPlanCurricula(Arrays.asList(alumnoBD.getPlanCurricular()));
        List<ResumenPlanCurricular> resumenPlanCurriculars = resumenPlanCurricularDAO.allByPlan(alumnoBD.getPlanCurricular());
        List<TipoCursoCurricula> tipoCursoCurriculas = tipoCursoCurriculaDAO.all();
        List<AlumnoAvanceCurricular> avanceCurriculars = alumnoAvanceCurricularDAO.allByAlumno(alumno);

        List<Alumno> alumnos = new ArrayList();
        alumnos.add(alumno);
        List<AlumnoCicloCurso> cursosVecesLlevado = alumnoCicloCursoDAO.allVecesLlevadoByAlumnos(alumnos);

        Map<String, AlumnoCicloCurso> mapCursosVecesLlevado = TypesUtil.convertListToMap("alumnoCursoKey", cursosVecesLlevado);

        for (AlumnoCicloCurso cursoAprobado : cursosAprobados) {
            cursoAprobado.setVecesCursadoTransient(0);
            AlumnoCicloCurso cursoVeces = mapCursosVecesLlevado.get(cursoAprobado.getAlumnoCursoKey());
            if (cursoVeces == null) {
                continue;
            }
            cursoAprobado.setVecesCursadoTransient(cursoVeces.getVecesCursado());
        }

        alumnoCursoSimultaneoDAO.deleteAllByAlumno(alumnoBD);
        List<AlumnoCursoCurricula> alumnoCursoCurriculas = alumnoCursoCurriculaDAO.allByAlumnoApro(alumnoBD);
        List<CursoOpcionalCurricula> cursoOpcionalCurriculas = mapCursoOpcionalAll.get(alumnoBD.getPlanCurricular().getId());

        avanceCurricularAsincronoService.procesarAlumnoSincrono(
                alumnoBD,
                mapCursoCurricula,
                mapRequisitoCursoCurricula,
                mapCursosEquivalentes,
                mapCursosVecesLlevado,
                cursosMatriculados,
                cursosAprobados,
                alumnoCursoCurriculas,
                cursoOpcionalCurriculas,
                mapCursoCurriculaByCurso,
                tipoCursoCurriculas,
                resumenPlanCurriculars,
                avanceCurriculars,
                cursoEquivalenteElectivos,
                mapCursoOpcionalAll,
                planCurriculars,
                mapCursoCurriculaAll,
                ds);
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
