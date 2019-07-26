package pe.edu.lamolina.pivot.controller.academico.avancecurricular;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.CursoEquivalente;
import pe.edu.lamolina.model.academico.CursoEquivalenteElectivo;
import pe.edu.lamolina.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.OrientacionCarrera;
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
        List<PlanCurricular> planesCurricular = planCurricularDAO.findById(planCurricular);
        List<PlanCurricular> planesCurriculars = planCurricularDAO.all();

        List<CursoCurricula> cursoCurriculasAll = cursoCurriculaDAO.allByPlanes(planesCurriculars);
        Map<Long, List<CursoCurricula>> mapCursoCurriculaAllPlanes = TypesUtil.convertListToMapList("planCurricular.id", cursoCurriculasAll);

        CicloAcademico cicloInicia = null;
        cicloInicia = planesCurricular.stream().map(x -> x.getCicloInicioVigencia()).min(Comparator.comparing(CicloAcademico::getCodigo)).get();

//        for (PlanCurricular plan : planesCurricular) {
//            CicloAcademico cicloPlan = plan.getCicloInicioVigencia();
//            if (cicloInicia == null) {
//                cicloInicia = cicloPlan;
//                continue;
//            }
//            if (cicloInicia.getCodigo().compareTo(cicloPlan.getCodigo()) == -1) {
//                cicloInicia = cicloPlan;
//            }
//        }
        Map<String, List<PlanCurricular>> mapPlanesByCiclo = TypesUtil.convertListToMapList("cicloInicioVigencia.codigo", planesCurricular);

        Map<String, CicloAcademico> mapCiclosPlanes = TypesUtil.convertListToMap("cicloInicioVigencia.codigo", "cicloInicioVigencia", planesCurricular);

        List<Alumno> alumnos = alumnoDAO.allByPlanCurricular(planCurricular);
        List<String> codigosCiclosPlanes = new ArrayList<String>(mapCiclosPlanes.keySet());

        Collections.sort(codigosCiclosPlanes);
        Collections.reverse(codigosCiclosPlanes);

        for (String intt : codigosCiclosPlanes) {

        }

        Map<Long, List<CursoCurricula>> mapCursoCurriculaAll = new LinkedHashMap();
        Map<Long, CursoCurricula> mapCursoCurriculaByCurso = new HashMap<>();
        Map<Long, List<RequisitoCursoCurricula>> mapRequisitoCursoCurriculaAll = new LinkedHashMap();
        Map<Long, List<CursoEquivalente>> mapCursosEquivalentesAll = new LinkedHashMap();
        int count = 0;

        this.obtenerDataVarios(planesCurricular, mapCursoCurriculaAll, mapRequisitoCursoCurriculaAll, mapCursosEquivalentesAll, mapCursoCurriculaByCurso);

        List<CursoEquivalenteElectivo> cursoEquivalenteElectivos = cursoEquivalenteElectivoDAO.allCursoPlanCurricula(planesCurricular);
        Map<Long, List<CursoEquivalenteElectivo>> mapEquivalenteElectivo = TypesUtil.convertListToMapList("cursoOpcionalCurricula.planCurricular.id", cursoEquivalenteElectivos);

        List<CursoOpcionalCurricula> cursoOpcionalCurriculas = cursoOpcionalCurriculaDAO.allByPlanCurricular(planesCurricular);
        Map<Long, List<CursoOpcionalCurricula>> mapCursoOpcional = TypesUtil.convertListToMapList("planCurricular.id", cursoOpcionalCurriculas);

        List<CursoOpcionalCurricula> cursoOpcionalAllPlanes = cursoOpcionalCurriculaDAO.allNotPlanCurricularAndCurso(planesCurricular);
        Map<Long, List<CursoOpcionalCurricula>> mapCursoOpcionalAll = TypesUtil.convertListToMapList("planCurricular.id", cursoOpcionalAllPlanes);

        List<MatriculaCurso> cursosMatriculados = matriculaCursoDAO.allActivoByAlumnosCicloActivo(alumnos);
        Map<Long, List<MatriculaCurso>> mapCursosMatriculados = TypesUtil.convertListToMapList("matriculaResumen.alumno.id", cursosMatriculados);

        List<AlumnoCicloCurso> cursosAprobados = alumnoCicloCursoDAO.allAprobadoActivoByAlumnos(alumnos);
        Map<String, AlumnoCicloCurso> mapCursosAprobadosKey = TypesUtil.convertListToMap("alumnoCursoKey", cursosAprobados);

        List<AlumnoCicloCurso> cursosDesapr = alumnoCicloCursoDAO.allDesaproActivoByAlumnos(alumnos);
        for (AlumnoCicloCurso alumnoCicloCurso : cursosDesapr) {
            if (mapCursosAprobadosKey.get(alumnoCicloCurso.getAlumnoCursoKey()) == null) {
                cursosAprobados.add(alumnoCicloCurso);
                mapCursosAprobadosKey.put(alumnoCicloCurso.getAlumnoCursoKey(), alumnoCicloCurso);
            }
        }
        Map<Long, List<AlumnoCicloCurso>> mapCursosAprobados = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", cursosAprobados);

        List<AlumnoCicloCurso> cursosVecesLlevado = alumnoCicloCursoDAO.allVecesLlevadoByAlumnos(alumnos);
        Map<String, AlumnoCicloCurso> mapTodosCursosVecesLlevado = TypesUtil.convertListToMap("alumnoCursoKey", cursosVecesLlevado);
        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCursosVecesLlevado = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", cursosVecesLlevado);

        List<AlumnoCursoCurricula> alumnoCursoCurriculas = alumnoCursoCurriculaDAO.allByAlumnosApr(alumnos);
        Map<Long, List<AlumnoCursoCurricula>> mapAlumnoCursoCurricula = TypesUtil.convertListToMapList("alumno.id", alumnoCursoCurriculas);

        for (AlumnoCicloCurso cursoAprobado : cursosAprobados) {
            cursoAprobado.setVecesCursadoTransient(0);
            AlumnoCicloCurso cursoVeces = mapTodosCursosVecesLlevado.get(cursoAprobado.getAlumnoCursoKey());
            if (cursoVeces == null) {
                continue;
            }
            cursoAprobado.setVecesCursadoTransient(cursoVeces.getVecesCursado());
        }
        List<TipoCursoCurricula> tipoCursoCurriculas = tipoCursoCurriculaDAO.all();

        List<AlumnoAvanceCurricular> alumnosAvanceCurriculars = alumnoAvanceCurricularDAO.allByAlumnos(alumnos);

        List<ResumenPlanCurricular> alumnosResumenPlanCurriculars = resumenPlanCurricularDAO.all();

        for (Alumno alumno : alumnos) {
            logger.debug("ALuMNO {}", alumno.getCodigo());
            OrientacionCarrera orientacionCarrera = alumno.getOrientacionCarrera();

            List<AlumnoAvanceCurricular> avanceCurriculars = alumnosAvanceCurriculars.stream().filter(x -> Objects.equals(x.getAlumno().getId(), alumno.getId())).collect(Collectors.toList());

            String codigoCicloAlumno = (String) ObjectUtil.getParentTree(alumno, "cicloIngreso.codigo");

            count++;
            if (Strings.isNullOrEmpty(codigoCicloAlumno)) {
                continue;
            }

            String codigoCicloPlan = this.getIndiceCicloAcademico(codigoCicloAlumno, codigosCiclosPlanes);
            if (codigoCicloPlan == null) {

                continue;
            }

            List<PlanCurricular> planesBD = mapPlanesByCiclo.get(codigoCicloPlan);
            if (planesBD.isEmpty()) {

                continue;
            }
            PlanCurricular planCurricularBD = null;
            if (orientacionCarrera != null) {
                planCurricularBD = planesBD.stream().filter(x -> Objects.equals(x.getOrientacionCarrera().getId(), orientacionCarrera.getId())).findAny().orElse(null);
            } else {
                planCurricularBD = planesBD.get(0);
            }

            PlanCurricular planBD = planCurricularBD;

            List<ResumenPlanCurricular> resumenPlanCurriculars = alumnosResumenPlanCurriculars.stream().filter(x -> Objects.equals(x.getPlanCurricular().getId(), planBD.getId())).collect(Collectors.toList());

            List<AlumnoCicloCurso> alumnoCursosVecesLlevado = fillList(mapAlumnoCursosVecesLlevado.get(alumno.getId()));
            Map<String, AlumnoCicloCurso> mapCursosVecesLlevado = TypesUtil.convertListToMap("alumnoCursoKey", alumnoCursosVecesLlevado);

            List<MatriculaCurso> cursosMatriculadosAlumno = fillList(mapCursosMatriculados.get(alumno.getId()));
            List<AlumnoCicloCurso> cursosAprobadosAlumno = fillList(mapCursosAprobados.get(alumno.getId()));
            List<CursoCurricula> cursosCurriculaPLan = fillList(mapCursoCurriculaAll.get(planBD.getId()));
            Map<Long, CursoCurricula> mapCursoCurriculaPlan = TypesUtil.convertListToMap("id", cursosCurriculaPLan);
            List<AlumnoCursoCurricula> alumnoCursoCurricula = mapAlumnoCursoCurricula.get(alumno.getId());
            List<CursoOpcionalCurricula> opcionalCurriculas = mapCursoOpcional.get(planBD.getId());
            List<CursoEquivalenteElectivo> equivalenteElectivos = mapEquivalenteElectivo.get(planBD.getId());

            avanceCurricularAsincronoService.crearAvanceCurricular(alumno,
                    planBD,
                    mapCursoCurriculaPlan,
                    mapRequisitoCursoCurriculaAll,
                    mapCursosEquivalentesAll,
                    mapCursosVecesLlevado,
                    cursosMatriculadosAlumno,
                    cursosAprobadosAlumno,
                    alumnoCursoCurricula,
                    opcionalCurriculas,
                    mapCursoCurriculaByCurso,
                    tipoCursoCurriculas,
                    resumenPlanCurriculars,
                    avanceCurriculars,
                    equivalenteElectivos,
                    mapCursoOpcionalAll,
                    planesCurriculars,
                    mapCursoCurriculaAllPlanes,
                    ds);
        }
    }

    private List fillList(List lista) {
        if (lista == null) {
            return new ArrayList();
        }
        return lista;
    }

    private String getIndiceCicloAcademico(String codigoCicloAlumno, List<String> codigosCiclosPlanes) {
        for (String codigoCicloPlan : codigosCiclosPlanes) {
            if (codigoCicloAlumno.compareTo(codigoCicloPlan) >= 0) {
                return codigoCicloPlan;
            }
        }
        return null;
    }

    private void obtenerDataVarios(
            List<PlanCurricular> planes,
            Map<Long, List<CursoCurricula>> mapCursoCurriculaAll,
            Map<Long, List<RequisitoCursoCurricula>> mapRequisitoCursoCurricula,
            Map<Long, List<CursoEquivalente>> mapCursosEquivalentes,
            Map<Long, CursoCurricula> mapCursoCurriculaByCurso) {

        List<RequisitoCursoCurricula> requisitoCursoCurriculas = requisitoCursoCurriculaDAO.allByPlanes(planes);
        Map<Long, List<RequisitoCursoCurricula>> mapRequisitoTemp = TypesUtil.convertListToMapList("cursoCurricula.id", requisitoCursoCurriculas);

        List<CursoEquivalente> cursoEquivalentes = cursoEquivalenteDAO.allActivoByPlanes(planes);
        Map<Long, List<CursoEquivalente>> mapEquivalentes = TypesUtil.convertListToMapList("cursoCurricula.id", cursoEquivalentes);

        List<CursoCurricula> cursosCurri = cursoCurriculaDAO.allByPlanes(planes);
        for (CursoCurricula cursoCurr : cursosCurri) {

            PlanCurricular plan = cursoCurr.getPlanCurricular();

            List<CursoCurricula> cursosCurriculaPlan = mapCursoCurriculaAll.get(plan.getId());
            if (cursosCurriculaPlan == null) {
                cursosCurriculaPlan = new ArrayList();
                mapCursoCurriculaAll.put(plan.getId(), cursosCurriculaPlan);
            }

            cursosCurriculaPlan.add(cursoCurr);

            List<RequisitoCursoCurricula> requisitos = fillList(mapRequisitoTemp.get(cursoCurr.getId()));
            cursoCurr.setRequisitosCursoCurricula(requisitos);
            mapRequisitoCursoCurricula.put(cursoCurr.getId(), requisitos);

            List<CursoEquivalente> equivalencias = fillList(mapEquivalentes.get(cursoCurr.getId()));
            mapCursosEquivalentes.put(cursoCurr.getId(), equivalencias);
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
// se usa tambien en Cambiar plan de infoAcademico
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

        List<MatriculaCurso> cursosMatriculados = matriculaCursoDAO.allActivoByAlumnoCicloActivo(alumnoBD);
        List<AlumnoCicloCurso> cursosAprobados = alumnoCicloCursoDAO.allAprobadoActivoByAlumno(alumnoBD);
        List<AlumnoCicloCurso> cursosDesapr = alumnoCicloCursoDAO.allDesaproActivoByAlumno(alumnoBD);
        for (AlumnoCicloCurso alumnoCicloCurso : cursosDesapr) {
            if (cursosAprobados.stream().filter(x -> Objects.equals(x.getCurso().getId(), alumnoCicloCurso.getCurso().getId())).findAny().orElse(null) == null) {
                cursosAprobados.add(alumnoCicloCurso);
            }
        }
        List<CursoEquivalenteElectivo> cursoEquivalenteElectivos = cursoEquivalenteElectivoDAO.allCursoPlanCurricula(Arrays.asList(alumnoBD.getPlanCurricular()));
        List<ResumenPlanCurricular> resumenPlanCurriculars = resumenPlanCurricularDAO.allByPlan(alumnoBD.getPlanCurricular());
        List<TipoCursoCurricula> tipoCursoCurriculas = tipoCursoCurriculaDAO.all();
        List<AlumnoAvanceCurricular> avanceCurriculars = alumnoAvanceCurricularDAO.allByAlumno(alumnoBD);

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

    @Override
    @Transactional
    public void generarAvanceCurricularByAlumnoPost(Alumno alumnoBD, DataSessionPivot ds) {
        alumnoBD = alumnoDAO.findAllInfo(alumnoBD.getId());
        Map<Long, CursoCurricula> mapCursoCurricula = new HashMap<>();
        Map<Long, CursoCurricula> mapCursoCurriculaByCurso = new HashMap<>();
        List<AlumnoCursoCurricula> alumnoCursoOld = alumnoCursoCurriculaDAO.allByAlumnoApro(alumnoBD);
        List<CursoCurricula> cursoCurriculas = cursoCurriculaDAO.allByPlanCurricular(alumnoBD.getPlanCurricular());
        List<CursoOpcionalCurricula> cursoOpcionaPlan = cursoOpcionalCurriculaDAO.allByPlanCurricular(alumnoBD.getPlanCurricular());
        List<TipoCursoCurricula> tipoCursoCurriculas = tipoCursoCurriculaDAO.all();
        obtenerDataPost(cursoCurriculas, mapCursoCurricula, mapCursoCurriculaByCurso);
        List<MatriculaCurso> cursosMatriculados = matriculaCursoDAO.allActivoByAlumnoCicloActivo(alumnoBD);
        List<AlumnoCicloCurso> cursosAprobados = alumnoCicloCursoDAO.allAprobadoActivoByAlumno(alumnoBD);
        List<ResumenPlanCurricular> resumenPlanCurriculars = resumenPlanCurricularDAO.allByPlan(alumnoBD.getPlanCurricular());
        List<AlumnoAvanceCurricular> avanceCurriculars = alumnoAvanceCurricularDAO.allByAlumno(alumnoBD);
        List<Alumno> alumnos = new ArrayList();
        alumnos.add(alumnoBD);
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

        avanceCurricularAsincronoService.procesarAlumnoSincronoPros(
                alumnoBD,
                mapCursoCurricula,
                mapCursosVecesLlevado,
                cursosMatriculados,
                cursosAprobados,
                mapCursoCurriculaByCurso,
                cursoOpcionaPlan,
                resumenPlanCurriculars,
                tipoCursoCurriculas,
                avanceCurriculars,
                alumnoCursoOld,
                ds);
    }

    private void obtenerDataPost(List<CursoCurricula> cursosCurricula, Map<Long, CursoCurricula> mapCursoCurricula, Map<Long, CursoCurricula> mapCursoCurriculaByCurso) {
        for (CursoCurricula cursoCurricula : cursosCurricula) {

            mapCursoCurricula.put(cursoCurricula.getId(), cursoCurricula);
            mapCursoCurriculaByCurso.put(cursoCurricula.getCurso().getId(), cursoCurricula);
        }
    }
}
