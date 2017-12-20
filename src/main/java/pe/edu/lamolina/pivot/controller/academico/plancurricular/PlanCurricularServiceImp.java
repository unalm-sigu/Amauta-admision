package pe.edu.lamolina.pivot.controller.academico.plancurricular;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ListsInspector;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoAdicionalCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoOpcionalCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.OrientacionCarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCurricularDAO;
import pe.edu.lamolina.pivot.dao.academico.RequisitoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.RequisitoCursoOpcionalDAO;
import pe.edu.lamolina.pivot.dao.academico.ResumenPlanCurricularDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.CursoAdicionalCurricula;
import pe.edu.lamolina.pivot.model.academico.CursoCurricula;
import pe.edu.lamolina.pivot.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.pivot.model.academico.OrientacionCarrera;
import pe.edu.lamolina.pivot.model.academico.PlanCurricular;
import pe.edu.lamolina.pivot.model.academico.RequisitoCursoCurricula;
import pe.edu.lamolina.pivot.model.academico.RequisitoCursoOpcional;
import pe.edu.lamolina.pivot.model.academico.ResumenPlanCurricular;
import pe.edu.lamolina.pivot.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import static pe.edu.lamolina.pivot.zelper.enums.EstadoEnum.ACT;
import static pe.edu.lamolina.pivot.zelper.enums.EstadoEnum.CRE;
import static pe.edu.lamolina.pivot.zelper.enums.EstadoEnum.INA;
import pe.edu.lamolina.pivot.zelper.enums.TipoCurriculaEnum;
import static pe.edu.lamolina.pivot.zelper.enums.TipoCursoCurriculaEnum.ELC;
import static pe.edu.lamolina.pivot.zelper.enums.TipoCursoCurriculaEnum.ELE;
import static pe.edu.lamolina.pivot.zelper.enums.TipoCursoCurriculaEnum.ELF;
import static pe.edu.lamolina.pivot.zelper.enums.TipoCursoCurriculaEnum.GEN;
import static pe.edu.lamolina.pivot.zelper.enums.TipoCursoCurriculaEnum.OBL;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class PlanCurricularServiceImp implements PlanCurricularService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    OrientacionCarreraDAO orientacionCarreraDAO;

    @Autowired
    PlanCurricularDAO planCurricularDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    TipoCursoCurriculaDAO tipoCursoCurriculaDAO;

    @Autowired
    CursoCurriculaDAO cursoCurriculaDAO;

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    RequisitoCursoCurriculaDAO requisitoCursoCurriculaDAO;

    @Autowired
    CursoAdicionalCurriculaDAO cursoAdicionalCurriculaDAO;

    @Autowired
    CursoOpcionalCurriculaDAO cursoOpcionalCurriculaDAO;

    @Autowired
    ResumenPlanCurricularDAO resumenPlanCurricularDAO;

    @Autowired
    RequisitoCursoOpcionalDAO requisitoCursoOpcionalDAO;

    @Override
    public List<Carrera> allCarreras(List<Carrera> carreras) {
        return carreraDAO.allRegularesByCarreras(carreras);
    }

    @Override
    public List<OrientacionCarrera> allOrientacionByCarreraEstado(Carrera carrera, EstadoEnum estadoEnum) {
        return orientacionCarreraDAO.allByCarreraEstado(carrera, estadoEnum);
    }

    @Override
    @Transactional
    public PlanCurricular savePlanCurricular(PlanCurricular planCurricular) {
        planCurricular.setEstadoEnum(EstadoEnum.CRE);

        if (ObjectUtil.getParentTree(planCurricular, "orientacionCarrera.id") == null) {
            planCurricular.setOrientacionCarrera(null);
        }

        planCurricularDAO.save(planCurricular);
        return planCurricular;
    }

    @Override
    @Transactional
    public void updatePlanCurricular(PlanCurricular planCurricular) {
        if (ObjectUtil.getParentTree(planCurricular, "orientacionCarrera.id") == null) {
            planCurricular.setOrientacionCarrera(null);
        }
        planCurricularDAO.updatePlanCurricular(planCurricular);
    }

    @Override
    @Transactional
    public void saveCursoCurricula(CursoCurricula cursoCurricula, DataSessionPivot ds) {
        verificarExistenciaCurso(cursoCurricula.getCurso(), cursoCurricula.getPlanCurricular());

        List<RequisitoCursoCurricula> requisitos = cursoCurricula.getCursosCurricula();
        requisitos = (requisitos == null) ? new ArrayList() : requisitos;

        for (RequisitoCursoCurricula requisito : requisitos) {
            requisito.setSimultaneo(requisito.getSimultaneo() == null ? 0 : 1);
            requisito.setCursoCurricula(cursoCurricula);
            requisito.setUserRegistro(ds.getUsuario());
            requisito.setFechaRegistro(new Date());
        }

        cursoCurricula.setUserRegistro(ds.getUsuario());
        cursoCurricula.setFechaRegistro(new Date());
        cursoCurriculaDAO.save(cursoCurricula);
        for (RequisitoCursoCurricula requisito : requisitos) {
            requisitoCursoCurriculaDAO.save(requisito);
        }

        ResumenPlanCurricular resumen = resumenPlanCurricularDAO.findByTipoCursoCurrPlan(
                cursoCurricula.getTipoCursoCurricula(),
                cursoCurricula.getPlanCurricular());

        if (resumen == null) {
            resumen = new ResumenPlanCurricular();
            resumen.setPlanCurricular(cursoCurricula.getPlanCurricular());
            resumen.setTipoCursoCurricula(cursoCurricula.getTipoCursoCurricula());
            resumen.setCreditos(cursoCurricula.getCreditos());
            resumen.setCursos(1);
            resumenPlanCurricularDAO.save(resumen);

        } else {
            resumen.setCreditos(resumen.getCreditos() + cursoCurricula.getCreditos());
            resumen.setCursos(resumen.getCursos() + 1);
            resumenPlanCurricularDAO.update(resumen);
        }

    }

    @Override
    @Transactional
    public void updateCursoCurricula(CursoCurricula cursoCurricula, DataSessionPivot ds) {
        CursoCurricula cursoCurriculaBD = cursoCurriculaDAO.find(cursoCurricula.getId());
        int diff = cursoCurricula.getCreditos() - cursoCurriculaBD.getCreditos();

        cursoCurriculaBD.setCreditosRequisito(cursoCurricula.getCreditosRequisito());
        cursoCurriculaDAO.update(cursoCurriculaBD);

        List<RequisitoCursoCurricula> requisitosDB = requisitoCursoCurriculaDAO.allByCursoCurricula(cursoCurricula);
        List<RequisitoCursoCurricula> requisitosForm = cursoCurricula.getCursosCurricula();
        ListsInspector inspector = TypesUtil.analizeLists(requisitosDB, requisitosForm, "cursoRequisito.id");
        Map<Long, RequisitoCursoCurricula> mapRequisitos = TypesUtil.convertListToMap("cursoRequisito.id", inspector.getListForm());

        List<RequisitoCursoCurricula> nuevos = inspector.getNewList();
        List<RequisitoCursoCurricula> eliminables = inspector.getDeadList();
        List<RequisitoCursoCurricula> existentes = inspector.getOldListDB();

        for (RequisitoCursoCurricula nuevo : nuevos) {
            nuevo.setSimultaneo(nuevo.getSimultaneo() == null ? 0 : 1);
            nuevo.setCursoCurricula(cursoCurricula);
            nuevo.setFechaRegistro(new Date());
            nuevo.setUserRegistro(ds.getUsuario());
            requisitoCursoCurriculaDAO.save(nuevo);
        }

        for (RequisitoCursoCurricula eliminable : eliminables) {
            requisitoCursoCurriculaDAO.delete(eliminable);
        }

        for (RequisitoCursoCurricula existenteBD : existentes) {
            RequisitoCursoCurricula existenteForm = mapRequisitos.get(existenteBD.getCursoRequisito().getId());
            existenteBD.setSimultaneo(existenteForm.getSimultaneo() == null ? 0 : 1);
            requisitoCursoCurriculaDAO.update(existenteBD);
        }

        if (diff != 0) {
            ResumenPlanCurricular resumen = resumenPlanCurricularDAO.findByTipoCursoCurrPlan(
                    cursoCurricula.getTipoCursoCurricula(),
                    cursoCurricula.getPlanCurricular());

            resumen.setCreditos(resumen.getCreditos() + diff);
            resumenPlanCurricularDAO.update(resumen);
        }
    }

    @Override
    @Transactional
    public void deleteCursoCurricula(CursoCurricula cursoCurricula, DataSessionPivot ds) {
        CursoCurricula cursoCurriculaBD = cursoCurriculaDAO.find(cursoCurricula.getId());

        ResumenPlanCurricular resumen = resumenPlanCurricularDAO.findByTipoCursoCurrPlan(
                cursoCurriculaBD.getTipoCursoCurricula(),
                cursoCurriculaBD.getPlanCurricular());

        resumen.setCreditos(resumen.getCreditos() - cursoCurriculaBD.getCreditos());
        resumen.setCursos(resumen.getCursos() - 1);
        resumenPlanCurricularDAO.update(resumen);

        cursoCurriculaDAO.delete(cursoCurriculaBD);

    }

    @Override
    @Transactional
    public void trasladarCiclo(CursoCurricula cursoCurricula, DataSessionPivot ds) {
        CursoCurricula cursoCurriculaBD = cursoCurriculaDAO.find(cursoCurricula.getId());
        PlanCurricular plan = cursoCurriculaBD.getPlanCurricular();

        Assert.isTrue(plan.getId().longValue() == cursoCurricula.getPlanCurricular().getId(), "El Plan de Estudios no es el correcto");
        Assert.isFalse(cursoCurricula.getNumeroCiclo() < 1, "El numero de ciclo no es el correcto");
        Assert.isFalse(cursoCurricula.getNumeroCiclo() > plan.getCiclos(), "El numero de ciclo no corresponde al plan");

        cursoCurriculaBD.setNumeroCiclo(cursoCurricula.getNumeroCiclo());
        cursoCurriculaDAO.update(cursoCurriculaBD);
    }

    @Override
    @Transactional
    public void trasladarToElectivos(CursoCurricula cursoCurricula, DataSessionPivot ds) {
        CursoCurricula cursoCurriculaBD = cursoCurriculaDAO.find(cursoCurricula.getId());
        List<RequisitoCursoCurricula> preRequisitos = requisitoCursoCurriculaDAO.allByCursoCurricula(cursoCurricula);
        List<RequisitoCursoCurricula> postRequisitos = requisitoCursoCurriculaDAO.allByRequisito(cursoCurricula);

        Assert.isTrue(postRequisitos.isEmpty(), "Este curso es pre-requisito de otros cursos. No puede ser trasladado");

        PlanCurricular plan = cursoCurriculaBD.getPlanCurricular();

        CursoOpcionalCurricula cursoOpcional = new CursoOpcionalCurricula();
        cursoOpcional.setCreditos(cursoCurriculaBD.getCreditos());
        cursoOpcional.setCreditosRequisito(cursoCurriculaBD.getCreditosRequisito());
        cursoOpcional.setCurso(cursoCurriculaBD.getCurso());
        cursoOpcional.setPlanCurricular(cursoCurriculaBD.getPlanCurricular());
        cursoOpcional.setTipoCursoCurricula(cursoCurricula.getTipoCursoCurricula());
        cursoOpcional.setFechaRegistro(new Date());
        cursoOpcional.setUserRegistro(ds.getUsuario());

        List<RequisitoCursoOpcional> newRequisitos = new ArrayList();
        for (RequisitoCursoCurricula requisito : preRequisitos) {
            RequisitoCursoOpcional newRequisito = new RequisitoCursoOpcional();
            newRequisito.setCursoRequisitoCurricula(requisito.getCursoRequisito());
            newRequisito.setCursoOpcional(cursoOpcional);
            newRequisito.setSimultaneo(requisito.getSimultaneo());
            newRequisito.setFechaRegistro(new Date());
            newRequisito.setUserRegistro(ds.getUsuario());
            newRequisitos.add(newRequisito);
        }

        ResumenPlanCurricular resumenA = resumenPlanCurricularDAO.findByTipoCursoCurrPlan(
                cursoCurriculaBD.getTipoCursoCurricula(),
                cursoCurriculaBD.getPlanCurricular());

        resumenA.setCreditos(resumenA.getCreditos() - cursoCurriculaBD.getCreditos());
        resumenA.setCursos(resumenA.getCursos() - 1);
        resumenPlanCurricularDAO.update(resumenA);

        for (RequisitoCursoCurricula requisito : preRequisitos) {
            requisitoCursoCurriculaDAO.delete(requisito);
        }
        cursoCurriculaDAO.delete(cursoCurriculaBD);

        cursoOpcionalCurriculaDAO.save(cursoOpcional);
        for (RequisitoCursoOpcional requisito : newRequisitos) {
            requisitoCursoOpcionalDAO.save(requisito);
        }
    }

    @Override
    @Transactional
    public void trasladarToObligatorios(CursoCurricula cursoCurriculaForm, DataSessionPivot ds) {
        CursoOpcionalCurricula cursoOpcionalBD = cursoOpcionalCurriculaDAO.find(cursoCurriculaForm.getId());
        List<RequisitoCursoOpcional> preRequisitos = requisitoCursoOpcionalDAO.allByCursoElectivo(cursoOpcionalBD);
        List<RequisitoCursoOpcional> postRequisitos = requisitoCursoOpcionalDAO.allPostRequisitosByCursoElectivo(cursoOpcionalBD);

        Assert.isTrue(postRequisitos.isEmpty(), "Este curso es pre-requisito de otros cursos. No puede ser trasladado");

        PlanCurricular plan = cursoOpcionalBD.getPlanCurricular();

        CursoCurricula cursoCurricula = new CursoCurricula();
        cursoCurricula.setCreditos(cursoOpcionalBD.getCreditos());
        cursoCurricula.setCreditosRequisito(cursoOpcionalBD.getCreditosRequisito());
        cursoCurricula.setCurso(cursoOpcionalBD.getCurso());
        cursoCurricula.setPlanCurricular(cursoOpcionalBD.getPlanCurricular());
        cursoCurricula.setTipoCursoCurricula(cursoCurriculaForm.getTipoCursoCurricula());
        cursoCurricula.setNumeroCiclo(cursoCurriculaForm.getNumeroCiclo());
        cursoCurricula.setFechaRegistro(new Date());
        cursoCurricula.setUserRegistro(ds.getUsuario());

        List<RequisitoCursoCurricula> newRequisitos = new ArrayList();
        for (RequisitoCursoOpcional requisito : preRequisitos) {
            Assert.isNull(requisito.getCursoRequisitoOpcional(), "No puede trasladarse este curso porque su requisito es un curso electivo");
            Assert.isTrue(requisito.getCursoRequisitoCurricula().getNumeroCiclo() < cursoCurriculaForm.getNumeroCiclo(),
                    "No puede trasladarse este curso porque su requisito pertence a un ciclo mayor al que desea trasladar");

            RequisitoCursoCurricula newRequisito = new RequisitoCursoCurricula();
            newRequisito.setCursoRequisito(requisito.getCursoRequisitoCurricula());
            newRequisito.setCursoCurricula(cursoCurricula);
            newRequisito.setSimultaneo(requisito.getSimultaneo());
            newRequisito.setFechaRegistro(new Date());
            newRequisito.setUserRegistro(ds.getUsuario());
            newRequisitos.add(newRequisito);
        }

        for (RequisitoCursoOpcional requisito : preRequisitos) {
            requisitoCursoOpcionalDAO.delete(requisito);
        }
        cursoOpcionalCurriculaDAO.delete(cursoOpcionalBD);

        cursoCurriculaDAO.save(cursoCurricula);
        for (RequisitoCursoCurricula requisito : newRequisitos) {
            requisitoCursoCurriculaDAO.save(requisito);
        }

        ResumenPlanCurricular resumen = resumenPlanCurricularDAO.findByTipoCursoCurrPlan(
                cursoCurricula.getTipoCursoCurricula(),
                cursoCurricula.getPlanCurricular());

        if (resumen == null) {
            resumen = new ResumenPlanCurricular();
            resumen.setPlanCurricular(plan);
            resumen.setTipoCursoCurricula(cursoCurriculaForm.getTipoCursoCurricula());
            resumen.setCreditos(cursoCurriculaForm.getCreditos());
            resumen.setCursos(1);
            resumenPlanCurricularDAO.save(resumen);

        } else {
            resumen.setCreditos(resumen.getCreditos() + cursoCurricula.getCreditos());
            resumen.setCursos(resumen.getCursos() + 1);
            resumenPlanCurricularDAO.update(resumen);
        }
    }

    @Override
    @Transactional
    public void saveCursoAdicional(CursoAdicionalCurricula cursoAdicional, DataSessionPivot ds) {
        verificarExistenciaCurso(cursoAdicional.getCurso(), cursoAdicional.getPlanCurricular());

        cursoAdicional.setUserRegistro(ds.getUsuario());
        cursoAdicional.setFechaRegistro(new Date());
        cursoAdicionalCurriculaDAO.save(cursoAdicional);
    }

    @Override
    @Transactional
    public void saveCursoOpcional(CursoOpcionalCurricula cursoOpcional, DataSessionPivot ds) {
        verificarExistenciaCurso(cursoOpcional.getCurso(), cursoOpcional.getPlanCurricular());

        List<RequisitoCursoOpcional> requisitos = cursoOpcional.getCursosOpcionales();
        requisitos = (requisitos == null) ? new ArrayList() : requisitos;

        for (RequisitoCursoOpcional requisito : requisitos) {
            ObjectUtil.eliminarAttrSinId(requisito, "cursoRequisitoCurricula");
            ObjectUtil.eliminarAttrSinId(requisito, "cursoRequisitoOpcional");

            requisito.setSimultaneo(requisito.getSimultaneo() == null ? 0 : 1);
            requisito.setCursoOpcional(cursoOpcional);
            requisito.setUserRegistro(ds.getUsuario());
            requisito.setFechaRegistro(new Date());
        }

        cursoOpcional.setUserRegistro(ds.getUsuario());
        cursoOpcional.setFechaRegistro(new Date());
        cursoOpcionalCurriculaDAO.save(cursoOpcional);
        for (RequisitoCursoOpcional requisito : requisitos) {
            requisitoCursoOpcionalDAO.save(requisito);
        }

    }

    @Override
    @Transactional
    public void updateCursoOpcional(CursoOpcionalCurricula cursoOpcional, DataSessionPivot ds) {
        CursoOpcionalCurricula cursoOpcionalBD = cursoOpcionalCurriculaDAO.find(cursoOpcional.getId());
        cursoOpcionalBD.setCreditosRequisito(cursoOpcional.getCreditosRequisito());
        cursoOpcionalCurriculaDAO.update(cursoOpcionalBD);

        List<RequisitoCursoOpcional> requisitosDB = requisitoCursoOpcionalDAO.allByCursoElectivo(cursoOpcional);
        List<RequisitoCursoOpcional> requisitosForm = cursoOpcional.getCursosOpcionales();
        ListsInspector inspector = TypesUtil.analizeLists(requisitosDB, requisitosForm, "cursoRequisito.id");
        Map<Long, RequisitoCursoOpcional> mapRequisitos = TypesUtil.convertListToMap("cursoRequisito.id", inspector.getListForm());

        List<RequisitoCursoOpcional> nuevos = inspector.getNewList();
        List<RequisitoCursoOpcional> eliminables = inspector.getDeadList();
        List<RequisitoCursoOpcional> existentes = inspector.getOldListDB();

        for (RequisitoCursoOpcional nuevo : nuevos) {
            ObjectUtil.eliminarAttrSinId(nuevo, "cursoRequisitoCurricula");
            ObjectUtil.eliminarAttrSinId(nuevo, "cursoRequisitoOpcional");

            nuevo.setSimultaneo(nuevo.getSimultaneo() == null ? 0 : 1);
            nuevo.setCursoOpcional(cursoOpcional);
            nuevo.setFechaRegistro(new Date());
            nuevo.setUserRegistro(ds.getUsuario());
            requisitoCursoOpcionalDAO.save(nuevo);
        }

        for (RequisitoCursoOpcional eliminable : eliminables) {
            requisitoCursoOpcionalDAO.delete(eliminable);
        }

        for (RequisitoCursoOpcional existenteBD : existentes) {
            ObjectUtil.eliminarAttrSinId(existenteBD, "cursoRequisitoCurricula");
            ObjectUtil.eliminarAttrSinId(existenteBD, "cursoRequisitoOpcional");

            RequisitoCursoOpcional existenteForm = mapRequisitos.get(existenteBD.getCursoRequisito().getId());
            existenteBD.setSimultaneo(existenteForm.getSimultaneo() == null ? 0 : 1);
            requisitoCursoOpcionalDAO.update(existenteBD);
        }
    }

    @Override
    public PlanCurricular findPlanCurricularById(PlanCurricular planCurricular) {
        PlanCurricular plan = planCurricularDAO.find(planCurricular.getId());
        List<CursoCurricula> cursosPlan = cursoCurriculaDAO.allByPlanCurricular(planCurricular);
        plan.setCursoCurricula(cursosPlan);
        return plan;
    }

    @Override
    public List<CicloAcademico> allUltimosCiclos(Integer cantidadCiclos) {
        return cicloAcademicoDAO.allUltimos(cantidadCiclos);
    }

    @Override
    public List<PlanCurricular> allByDynatable(DynatableFilter filter, List<Carrera> carreras) {
        List<PlanCurricular> planesCurriculares = planCurricularDAO.allByDynatable(filter, carreras);
        Map<Long, Integer> cursosCurriculaCounts = cursoCurriculaDAO.countByPlanesCurricular(planesCurriculares);
        Map<Long, Integer> cursosAdiCurriculaCounts = cursoAdicionalCurriculaDAO.countByPlanesCurricular(planesCurriculares);
        Map<Long, Integer> cursosOpcCurriculaCounts = cursoOpcionalCurriculaDAO.countByPlanesCurricular(planesCurriculares);

        for (PlanCurricular planCurricular : planesCurriculares) {
            Integer curObl = cursosCurriculaCounts.get(planCurricular.getId());
            Integer curOpc = cursosOpcCurriculaCounts.get(planCurricular.getId());
            Integer curAdc = cursosAdiCurriculaCounts.get(planCurricular.getId());
            planCurricular.setCantidadCursosCurricula(curObl == null ? 0 : curObl);
            planCurricular.setCantidadCursosOpcionales(curOpc == null ? 0 : curOpc);
            planCurricular.setCantidadCursosAdicionales(curAdc == null ? 0 : curAdc);
        }
        return planesCurriculares;
    }

    @Override
    public List<CursoCurricula> allCursosOblByDynatable(DynatableFilter filter) {
        if (filter.getQueries() == null) {
            return new ArrayList();
        }

        List<CursoCurricula> cursosCurricula = cursoCurriculaDAO.allByDynatable(filter);
        List<RequisitoCursoCurricula> preRequisitos = requisitoCursoCurriculaDAO.allByCursosCurricula(cursosCurricula);
        List<RequisitoCursoCurricula> postRequisitos = requisitoCursoCurriculaDAO.allPostRequisitosByCursosCurricula(cursosCurricula);
        List<RequisitoCursoOpcional> postRequisitosOpc = requisitoCursoOpcionalDAO.allPostRequisitosByCursosCurricula(cursosCurricula);

        Map<Long, List<RequisitoCursoCurricula>> mapPreRequisitos = TypesUtil.convertListToMapList("cursoCurricula.id", preRequisitos);
        Map<Long, List<RequisitoCursoCurricula>> mapPostRequisitos = TypesUtil.convertListToMapList("cursoRequisito.id", postRequisitos);
        Map<Long, List<RequisitoCursoOpcional>> mapPostRequisitosOpc = TypesUtil.convertListToMapList("cursoRequisitoCurricula.id", postRequisitosOpc);

        for (CursoCurricula curso : cursosCurricula) {
            List<RequisitoCursoCurricula> preRequisitosCurso = mapPreRequisitos.get(curso.getId());
            List<RequisitoCursoCurricula> postRequisitosCurso = mapPostRequisitos.get(curso.getId());
            List<RequisitoCursoOpcional> postRequisitosCursoOpc = mapPostRequisitosOpc.get(curso.getId());

            curso.setCursosCurricula(preRequisitosCurso == null ? new ArrayList() : preRequisitosCurso);
            curso.setRequisitosCursoCurricula(postRequisitosCurso == null ? new ArrayList() : postRequisitosCurso);
            curso.setRequisitosCursoOpcional(postRequisitosCursoOpc == null ? new ArrayList() : postRequisitosCursoOpc);
        }

        return cursosCurricula;
    }

    @Override
    public List<ResumenPlanCurricular> allResPlanCurByDynatable(DynatableFilter filter) {
        if (filter.getQueries() == null) {
            return new ArrayList();
        }
        return resumenPlanCurricularDAO.allByDynatable(filter);
    }

    @Override
    public List<CursoAdicionalCurricula> allCursosAdcByDynatable(DynatableFilter filter) {
        if (filter.getQueries() == null) {
            return new ArrayList();
        }
        return cursoAdicionalCurriculaDAO.allByDynatable(filter);
    }

    @Override
    public List<CursoOpcionalCurricula> allCursosElecByDynatable(DynatableFilter filter) {
        if (filter.getQueries() == null) {
            return new ArrayList();
        }

        List<CursoOpcionalCurricula> cursosElectivos = cursoOpcionalCurriculaDAO.allByDynatable(filter);
        List<RequisitoCursoOpcional> preRequisitos = requisitoCursoOpcionalDAO.allRequisitosByCursosElectivos(cursosElectivos);
        List<RequisitoCursoOpcional> postRequisitos = requisitoCursoOpcionalDAO.allPostRequisitosByCursosElectivo(cursosElectivos);

        Map<Long, List<RequisitoCursoOpcional>> mapRequisitos = TypesUtil.convertListToMapList("cursoOpcional.id", preRequisitos);
        Map<Long, List<RequisitoCursoOpcional>> mapPostRequisitos = TypesUtil.convertListToMapList("cursoRequisitoOpcional.id", postRequisitos);

        for (CursoOpcionalCurricula cursoElectivo : cursosElectivos) {
            List<RequisitoCursoOpcional> preRequisitosElec = mapRequisitos.get(cursoElectivo.getId());
            List<RequisitoCursoOpcional> postRequisitosElec = mapPostRequisitos.get(cursoElectivo.getId());
            cursoElectivo.setCursosOpcionales(preRequisitosElec == null ? new ArrayList() : preRequisitosElec);
            cursoElectivo.setRequisitosCursoOpcionales(postRequisitosElec == null ? new ArrayList() : postRequisitosElec);
        }

        return cursosElectivos;
    }

    @Override
    public List<TipoCursoCurricula> allTiposCursoCurricula() {
        return tipoCursoCurriculaDAO.all();
    }

    @Override
    public List<TipoCursoCurricula> allTiposCursoCurriculasElectivos() {
        List<TipoCursoCurricula> total = tipoCursoCurriculaDAO.all();
        List<TipoCursoCurricula> lista = new ArrayList();
        for (TipoCursoCurricula tipo : total) {
            if (Arrays.asList(ELE, ELF, ELC).contains(tipo.getCodigoEnum())) {
                lista.add(tipo);
            }
        }
        return lista;
    }

    @Override
    public List<TipoCursoCurricula> allTiposCursoCurriculasObligatorios() {
        List<TipoCursoCurricula> total = tipoCursoCurriculaDAO.all();
        List<TipoCursoCurricula> lista = new ArrayList();
        for (TipoCursoCurricula tipo : total) {
            if (Arrays.asList(OBL, GEN).contains(tipo.getCodigoEnum())) {
                lista.add(tipo);
            }
        }
        return lista;
    }

    @Override
    public TipoCursoCurricula findTipoCurricula(Long tipoCursoCurricula) {
        return tipoCursoCurriculaDAO.find(tipoCursoCurricula);
    }

    @Override
    public List<CursoCurricula> allCursosCurriculaByFilter(TipoCursoCurricula tipoCursoCurricula) {
        return cursoCurriculaDAO.allByFilter(tipoCursoCurricula);
    }

    @Override
    public List<Curso> allCursoByNombreTipoCurricula(String nombre, List<TipoCurriculaEnum> tiposCurriculaEnum) {
        List<String> tiposCurricula = null;
        if (tiposCurriculaEnum != null && !tiposCurriculaEnum.isEmpty()) {
            tiposCurricula = new ArrayList();
            for (TipoCurriculaEnum tipoCurriculaEnum : tiposCurriculaEnum) {
                tiposCurricula.add(tipoCurriculaEnum.name());
            }
        }
        return cursoDAO.allByNombreTipoCurricula(nombre, tiposCurricula, 10);
    }

    @Override
    public List<CursoCurricula> allCursoCurriculaByNombre(CursoCurricula cursoCurricula) {
        return cursoCurriculaDAO.allByNombrePlanNroCiclo(cursoCurricula, 10);
    }

    @Override
    public List<RequisitoCursoOpcional> allCursosObligatoriosAndElectivosByNombre(CursoCurricula cursoCurriculaForm) {
        List<RequisitoCursoOpcional> cursos = new ArrayList();
        List<CursoCurricula> cursosObligatorios = cursoCurriculaDAO.allByNombrePlanNroCiclo(cursoCurriculaForm, 10);
        for (CursoCurricula obligatorio : cursosObligatorios) {
            RequisitoCursoOpcional curso = new RequisitoCursoOpcional();
            curso.setCursoRequisitoCurricula(obligatorio);
            cursos.add(curso);
        }
        List<CursoOpcionalCurricula> cursosElectivos = cursoOpcionalCurriculaDAO.allByNombrePlan(cursoCurriculaForm, 10);
        for (CursoOpcionalCurricula electivo : cursosElectivos) {
            RequisitoCursoOpcional curso = new RequisitoCursoOpcional();
            curso.setCursoRequisitoOpcional(electivo);
            cursos.add(curso);
        }

        Collections.sort(cursos, new RequisitoCursoOpcional.CompareNombre());
        return cursos;
    }

    @Override
    public CursoCurricula findCursoCurricula(Long cursoCurriculaId) {
        CursoCurricula cursoCurricula = cursoCurriculaDAO.find(cursoCurriculaId);
        List<RequisitoCursoCurricula> requisitos = requisitoCursoCurriculaDAO.allByCursoCurricula(cursoCurricula);
        cursoCurricula.setCursosCurricula(requisitos);
        return cursoCurricula;
    }

    @Override
    public CursoOpcionalCurricula findCursoElectivo(Long cursoElectivoId) {
        CursoOpcionalCurricula cursoElectivo = cursoOpcionalCurriculaDAO.find(cursoElectivoId);
        List<RequisitoCursoOpcional> requisitos = requisitoCursoOpcionalDAO.allByCursoElectivo(cursoElectivo);
        cursoElectivo.setCursosOpcionales(requisitos);
        return cursoElectivo;
    }

    @Override
    @Transactional
    public void deleteCursoAdicional(Long cursoAdicionalId) {
        cursoAdicionalCurriculaDAO.delete(new CursoAdicionalCurricula(cursoAdicionalId));
    }

    @Override
    @Transactional
    public void deleteCursoOpcional(CursoOpcionalCurricula cursoElectivo) {
        CursoOpcionalCurricula electivoBD = cursoOpcionalCurriculaDAO.find(cursoElectivo.getId());
        List<RequisitoCursoOpcional> requisitos = requisitoCursoOpcionalDAO.allByCursoElectivo(electivoBD);
        for (RequisitoCursoOpcional requisito : requisitos) {
            requisitoCursoOpcionalDAO.delete(requisito);
        }
        cursoOpcionalCurriculaDAO.delete(electivoBD);
    }

    @Override
    public List<Curso> allCursosByCodigo(String codigo) {
        return cursoDAO.allByCodigo(codigo);
    }

    private CursoCurricula findCursoCurriculaByCursoPlan(Curso curso, PlanCurricular planCurricular) {
        List<CursoCurricula> cursosCurricula = cursoCurriculaDAO.allByPlanCurricular(planCurricular);
        Map<Long, CursoCurricula> mapCursosCurricula = TypesUtil.convertListToMap("curso.id", cursosCurricula);
        return mapCursosCurricula.get(curso.getId());
    }

    private CursoOpcionalCurricula findCursoOpcionalByCursoPlan(Curso curso, PlanCurricular planCurricular) {
        List<CursoOpcionalCurricula> cursosOpcionales = cursoOpcionalCurriculaDAO.allByPlanCurricular(planCurricular);
        Map<Long, CursoOpcionalCurricula> mapCursosOpcionales = TypesUtil.convertListToMap("curso.id", cursosOpcionales);
        return mapCursosOpcionales.get(curso.getId());
    }

    private void verificarExistenciaCurso(Curso curso, PlanCurricular planCurricular) {
        CursoCurricula cursoCurricula = findCursoCurriculaByCursoPlan(curso, planCurricular);
        if (cursoCurricula != null && Arrays.asList(ELE, ELF, ELC).contains(cursoCurricula.getTipoCursoCurricula().getCodigoEnum())) {
        } else {
            Assert.isNull(cursoCurricula, "Este curso ya existe en el grupo de obligatorios o generales");
        }

        CursoOpcionalCurricula cursoOpcional = findCursoOpcionalByCursoPlan(curso, planCurricular);
        Assert.isNull(cursoOpcional, "Este curso ya existe en el grupo de electivos");

        List<CursoAdicionalCurricula> cursosAdicionales = cursoAdicionalCurriculaDAO.allByPlanCurricular(planCurricular);
        Map<Long, CursoAdicionalCurricula> mapCursosAdicionales = TypesUtil.convertListToMap("curso.id", cursosAdicionales);
        CursoAdicionalCurricula cursoAdicional = mapCursosAdicionales.get(curso.getId());
        Assert.isNull(cursoAdicional, "Este curso ya existe en el grupo de adicionales");

    }

    @Override
    @Transactional
    public void deletePlanCurricular(PlanCurricular plan) {
        PlanCurricular planBD = planCurricularDAO.find(plan.getId());
        Assert.isTrue(planBD.getEstadoEnum() == EstadoEnum.CRE, "Solo puede eliminarse un plan con estado Creado");

        List<CursoCurricula> cursos = cursoCurriculaDAO.allByPlanCurricular(planBD);
        List<CursoAdicionalCurricula> adicionales = cursoAdicionalCurriculaDAO.allByPlanCurricular(planBD);
        List<CursoOpcionalCurricula> opcionales = cursoOpcionalCurriculaDAO.allByPlanCurricular(planBD);
        List<ResumenPlanCurricular> resumenes = resumenPlanCurricularDAO.allByPlan(planBD);

        List<RequisitoCursoCurricula> requisitos = requisitoCursoCurriculaDAO.allByCursosCurricula(cursos);
        List<RequisitoCursoOpcional> requisitosOpc = requisitoCursoOpcionalDAO.allRequisitosByCursosElectivos(opcionales);

        for (RequisitoCursoOpcional req : requisitosOpc) {
            requisitoCursoOpcionalDAO.delete(req);
        }
        for (RequisitoCursoCurricula req : requisitos) {
            requisitoCursoCurriculaDAO.delete(req);
        }
        for (CursoAdicionalCurricula adi : adicionales) {
            cursoAdicionalCurriculaDAO.delete(adi);
        }
        for (CursoOpcionalCurricula opcional : opcionales) {
            cursoOpcionalCurriculaDAO.delete(opcional);
        }
        for (CursoCurricula curso : cursos) {
            cursoCurriculaDAO.delete(curso);
        }
        for (ResumenPlanCurricular resumen : resumenes) {
            resumenPlanCurricularDAO.delete(resumen);
        }
        planCurricularDAO.delete(planBD);
    }

    @Override
    @Transactional
    public void desactivarPlanCurricular(PlanCurricular plan) {
        PlanCurricular planBD = planCurricularDAO.find(plan.getId());
        Assert.isTrue(planBD.getEstadoEnum() == EstadoEnum.ACT, "Solo puede desactivarse un plan con estado Activo");
        planBD.setEstadoEnum(EstadoEnum.INA);
        planCurricularDAO.update(planBD);
    }

    @Override
    @Transactional
    public PlanCurricular clonarPlanCurricular(PlanCurricular pp, DataSessionPivot ds) {
        PlanCurricular plan = planCurricularDAO.find(pp.getId());
        Assert.isTrue(Arrays.asList(ACT, INA).contains(plan.getEstadoEnum()), "No está permitido clonar planes curriculares a partir de este");

        PlanCurricular nn = new PlanCurricular();
        nn.setCarrera(plan.getCarrera());
        nn.setCiclos(plan.getCiclos());
        nn.setOrientacionCarrera(plan.getOrientacionCarrera());
        nn.setEstadoEnum(CRE);

        nn.setCursoCurricula(new ArrayList());
        nn.setCursoAdicionalCurricula(new ArrayList());
        nn.setCursoOpcionalCurricula(new ArrayList());

        List<CursoCurricula> cursos = cursoCurriculaDAO.allByPlanCurricular(plan);
        List<CursoAdicionalCurricula> adicionales = cursoAdicionalCurriculaDAO.allByPlanCurricular(plan);
        List<CursoOpcionalCurricula> opcionales = cursoOpcionalCurriculaDAO.allByPlanCurricular(plan);
        List<ResumenPlanCurricular> resumenes = resumenPlanCurricularDAO.allByPlan(plan);

        List<RequisitoCursoCurricula> requisitos = requisitoCursoCurriculaDAO.allByCursosCurricula(cursos);
        List<RequisitoCursoOpcional> requisitosOpc = requisitoCursoOpcionalDAO.allRequisitosByCursosElectivos(opcionales);

        Map<Long, CursoCurricula> mapCursoCurricula = new LinkedHashMap();
        for (CursoCurricula curso : cursos) {
            CursoCurricula cc = new CursoCurricula();
            cc.setCreditos(curso.getCreditos());
            cc.setCreditosCurriculaRequisito(curso.getCreditosCurriculaRequisito());
            cc.setCreditosRequisito(curso.getCreditosRequisito());
            cc.setFechaRegistro(new Date());
            cc.setNumeroCiclo(curso.getNumeroCiclo());
            cc.setCurso(curso.getCurso());
            cc.setPlanCurricular(nn);
            cc.setTipoCursoCurricula(curso.getTipoCursoCurricula());
            cc.setUserRegistro(ds.getUsuario());
            cc.setCursosCurricula(new ArrayList());

            mapCursoCurricula.put(cc.getCurso().getId(), cc);
            nn.getCursoCurricula().add(cc);
        }

        for (CursoAdicionalCurricula adi : adicionales) {
            CursoAdicionalCurricula aa = new CursoAdicionalCurricula();
            aa.setCurso(adi.getCurso());
            aa.setFechaRegistro(new Date());
            aa.setUserRegistro(ds.getUsuario());
            aa.setPlanCurricular(nn);

            nn.getCursoAdicionalCurricula().add(aa);
        }

        for (CursoOpcionalCurricula opc : opcionales) {
            CursoOpcionalCurricula oo = new CursoOpcionalCurricula();
            oo.setCreditos(opc.getCreditos());
            oo.setCreditosCurriculaRequisito(opc.getCreditosCurriculaRequisito());
            oo.setCreditosRequisito(opc.getCreditosRequisito());
            oo.setCurso(opc.getCurso());
            oo.setFechaRegistro(new Date());
            oo.setPlanCurricular(nn);
            oo.setTipoCursoCurricula(opc.getTipoCursoCurricula());
            oo.setUserRegistro(ds.getUsuario());
            oo.setRequisitosCursoOpcionales(new ArrayList());

            nn.getCursoOpcionalCurricula().add(oo);
        }

        for (RequisitoCursoCurricula req : requisitos) {
            RequisitoCursoCurricula r = new RequisitoCursoCurricula();
            CursoCurricula cc = mapCursoCurricula.get(req.getCursoCurricula().getCurso().getId());
            r.setCursoCurricula(cc);
            r.setCursoRequisito(mapCursoCurricula.get(req.getCursoRequisito().getCurso().getId()));
            r.setSimultaneo(req.getSimultaneo());
            r.setFechaRegistro(new Date());
            r.setUserRegistro(ds.getUsuario());

            cc.getCursosCurricula().add(r);
        }

        return nn;
    }

}
