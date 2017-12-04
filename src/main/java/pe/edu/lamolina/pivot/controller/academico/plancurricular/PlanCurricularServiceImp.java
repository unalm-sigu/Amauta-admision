package pe.edu.lamolina.pivot.controller.academico.plancurricular;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.pivot.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoAdicionalCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoOpcionalCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.OrientacionCarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCurricularDAO;
import pe.edu.lamolina.pivot.dao.academico.RequisitoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.ResumenPlanCurricularDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.model.academico.AnexoBoletin;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.CursoAdicionalCurricula;
import pe.edu.lamolina.pivot.model.academico.CursoCurricula;
import pe.edu.lamolina.pivot.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.academico.OrientacionCarrera;
import pe.edu.lamolina.pivot.model.academico.PlanCurricular;
import pe.edu.lamolina.pivot.model.academico.RequisitoCursoCurricula;
import pe.edu.lamolina.pivot.model.academico.ResumenPlanCurricular;
import pe.edu.lamolina.pivot.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoCurriculaEnum;

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

    @Override
    public List<Carrera> allCarrerasByFilter(Facultad facultad, EstadoEnum estadoEnum) {
        return carreraDAO.allByFilter(facultad, estadoEnum);
    }

    @Override
    public List<OrientacionCarrera> allOrientacionCarreraByFilter(Carrera carrera, EstadoEnum estadoEnum) {
        return orientacionCarreraDAO.allByFilter(carrera, estadoEnum);
    }

    @Override
    @Transactional(readOnly = false)
    public PlanCurricular savePlanCurricular(PlanCurricular planCurricular) {
        planCurricular.setEstadoEnum(EstadoEnum.CRE);

        if (ObjectUtil.getParentTree(planCurricular, "orientacionCarrera.id") == null) {
            planCurricular.setOrientacionCarrera(null);
        }

        planCurricularDAO.save(planCurricular);
        return planCurricular;
    }

    @Override
    @Transactional(readOnly = false)
    public void updatePlanCurricular(PlanCurricular planCurricular) {
        if (ObjectUtil.getParentTree(planCurricular, "orientacionCarrera.id") == null) {
            planCurricular.setOrientacionCarrera(null);
        }
        planCurricularDAO.updatePlanCurricular(planCurricular);
    }

    @Override
    @Transactional(readOnly = false)
    public void agregarCursoCurricula(CursoCurricula cursoCurricula) {

        PlanCurricular planCurricular = planCurricularDAO.find(cursoCurricula.getPlanCurricular().getId());
        List<CursoCurricula> cursosCurricula = cursoCurriculaDAO.allByPlan(planCurricular);
        CursoCurricula cursoCurriculaFound = cursosCurricula.stream().filter(curcur -> curcur.getCurso().getId().equals(cursoCurricula.getCurso().getId())).findFirst().orElse(null);

        if (cursoCurriculaFound != null) {
            throw new PhobosException("El curso ya se encuentra agregado en el plan curricular");
        }

        if (cursoCurricula.getRequisitosCurricula() != null && !cursoCurricula.getRequisitosCurricula().isEmpty()) {
            for (RequisitoCursoCurricula reqCurricula : cursoCurricula.getRequisitosCurricula()) {
                reqCurricula.setCursoCurricula(cursoCurricula);
            }
        }

        cursoCurriculaDAO.save(cursoCurricula);
        //    cursoCurricula = cursoCurriculaDAO.find(cursoCurricula.getId());
        ResumenPlanCurricular resumenPlanCurricular = resumenPlanCurricularDAO.findByTipoCurCurPlan(
                cursoCurricula.getTipoCursoCurricula(),
                cursoCurricula.getPlanCurricular());

        if (resumenPlanCurricular == null) {
            resumenPlanCurricular = new ResumenPlanCurricular();
            resumenPlanCurricular.setPlanCurricular(cursoCurricula.getPlanCurricular());
            resumenPlanCurricular.setTipoCursoCurricula(cursoCurricula.getTipoCursoCurricula());
            resumenPlanCurricular.setCreditos(cursoCurricula.getCreditos());
            resumenPlanCurricularDAO.save(resumenPlanCurricular);
        } else {
            Integer creditos = resumenPlanCurricular.getCreditos() + cursoCurricula.getCreditos();
            resumenPlanCurricular.setCreditos(creditos);
            resumenPlanCurricularDAO.update(resumenPlanCurricular);
        }

    }

    @Override
    @Transactional(readOnly = false)
    public void agregarCursoAdcCurricula(CursoAdicionalCurricula cursoAdicionalCurricula) {
        PlanCurricular planCurricular = planCurricularDAO.find(cursoAdicionalCurricula.getPlanCurricular().getId());
        List<CursoAdicionalCurricula> cursosAdicionalesPlan = cursoAdicionalCurriculaDAO.allByPlan(planCurricular);

        CursoAdicionalCurricula cursoAdcCurriculaFound = cursosAdicionalesPlan.stream().filter(curadc -> curadc.getCurso().getId().equals(cursoAdicionalCurricula.getCurso().getId())).findFirst().orElse(null);

        if (cursoAdcCurriculaFound != null) {
            throw new PhobosException("El curso adicional ya se encuentra agregado en el plan curricular");
        }

        cursoAdicionalCurriculaDAO.save(cursoAdicionalCurricula);
    }

    @Override
    @Transactional(readOnly = false)
    public void agregarCursoOpcCurricula(CursoOpcionalCurricula cursoOpcionalCurricula) {
        cursoOpcionalCurriculaDAO.save(cursoOpcionalCurricula);
    }

    @Override
    @Transactional(readOnly = false)
    public void updateCursoCurricula(CursoCurricula cursoCurricula) {
        CursoCurricula cursoCurriculaDB = cursoCurriculaDAO.find(cursoCurricula.getId());
        List<RequisitoCursoCurricula> requisitosDB = requisitoCursoCurriculaDAO.allByCursoCurricula(cursoCurricula);

        cursoCurriculaDAO.updateCreditoRequisito(cursoCurricula);

        for (RequisitoCursoCurricula requisitoDB : requisitosDB) {

            RequisitoCursoCurricula requisitoFound = null;
            if (cursoCurricula.getRequisitosCurricula() != null && !cursoCurricula.getRequisitosCurricula().isEmpty()) {
                requisitoFound = cursoCurricula.getRequisitosCurricula().stream().filter(req -> requisitoDB.getId().equals(req.getId())).findFirst().orElse(null);
            }
            if (requisitoFound == null) {
                requisitoCursoCurriculaDAO.delete(requisitoDB);
            }

        }

        if (cursoCurricula.getRequisitosCurricula() != null && !cursoCurricula.getRequisitosCurricula().isEmpty()) {
            for (RequisitoCursoCurricula reqCurricula : cursoCurricula.getRequisitosCurricula()) {
                reqCurricula.setCursoCurricula(cursoCurricula);
                if (reqCurricula.getId() == null) {
                    requisitoCursoCurriculaDAO.save(reqCurricula);
                }
            }
        }
    }

    @Override
    public PlanCurricular findPlanCurricularById(PlanCurricular planCurricular) {
        return planCurricularDAO.find(planCurricular.getId());
    }

    @Override
    public List<CicloAcademico> allRecientesCiclosAcad(Integer year, Integer limit) {
        return cicloAcademicoDAO.allRecientes(year, limit);
    }

    @Override
    public List<PlanCurricular> allByDynatable(DynatableFilter filter, List<Carrera> carreras) {
        List<PlanCurricular> planesCurriculares = planCurricularDAO.allByDynatable(filter, carreras);
        Map cursosCurriculaCounts = cursoCurriculaDAO.countByPlanesCurricular(planesCurriculares);
        Map cursosAdiCurriculaCounts = cursoAdicionalCurriculaDAO.countByPlanesCurricular(planesCurriculares);
        Map cursosOpcCurriculaCounts = cursoOpcionalCurriculaDAO.countByPlanesCurricular(planesCurriculares);
        for (PlanCurricular planCurricular : planesCurriculares) {
            Integer curObl = (Integer) cursosCurriculaCounts.get(planCurricular.getId());
            Integer curOpc = (Integer) cursosOpcCurriculaCounts.get(planCurricular.getId());
            Integer curAdc = (Integer) cursosAdiCurriculaCounts.get(planCurricular.getId());
            planCurricular.setCantidadCursosCurricula(curObl == null ? 0 : curObl);
            planCurricular.setCantidadCursosOpcionales(curOpc == null ? 0 : curOpc);
            planCurricular.setCantidadCursosAdicionales(curAdc == null ? 0 : curAdc);
        }
        return planesCurriculares;
    }

    @Override
    public List<CursoCurricula> allCursosOblByDynatable(DynatableFilter filter) {
        if (filter.getQueries() == null) {
            return new ArrayList<CursoCurricula>();
        }
        return cursoCurriculaDAO.allByDynatable(filter);
    }

    @Override
    public List<ResumenPlanCurricular> allResPlanCurByDynatable(DynatableFilter filter) {
        if (filter.getQueries() == null) {
            return new ArrayList<ResumenPlanCurricular>();
        }
        return resumenPlanCurricularDAO.allByDynatable(filter);
    }

    @Override
    public List<CursoAdicionalCurricula> allCursosAdcByDynatable(DynatableFilter filter) {
        if (filter.getQueries() == null) {
            return new ArrayList<CursoAdicionalCurricula>();
        }
        return cursoAdicionalCurriculaDAO.allByDynatable(filter);
    }

    @Override
    public List<CursoOpcionalCurricula> allCursosElecByDynatable(DynatableFilter filter) {
        if (filter.getQueries() == null) {
            return new ArrayList<CursoOpcionalCurricula>();
        }
        return cursoOpcionalCurriculaDAO.allByDynatable(filter);
    }

    @Override
    public List<TipoCursoCurricula> allTiposCursoCurricula() {
        return tipoCursoCurriculaDAO.all();
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
            tiposCurricula = new ArrayList<>();
            for (TipoCurriculaEnum tipoCurriculaEnum : tiposCurriculaEnum) {
                tiposCurricula.add(tipoCurriculaEnum.name());
            }
        }
        return cursoDAO.allByNombreFilter(nombre, tiposCurricula, 10);
    }

    @Override
    public List<CursoCurricula> allCursoCurriculaByNombre(Long planCurriculaId, Integer numeroCiclo, String nombre) {
        return cursoCurriculaDAO.allByNombreFilter(planCurriculaId, numeroCiclo, nombre, 10);
    }

    @Override
    public CursoCurricula findCursoCurricula(Long cursoCurriculaId) {
        CursoCurricula cursoCurricula = cursoCurriculaDAO.find(cursoCurriculaId);
        List<RequisitoCursoCurricula> requisitos = requisitoCursoCurriculaDAO.allByCursoCurricula(cursoCurricula);
        cursoCurricula.setRequisitosCurricula(requisitos);
        return cursoCurricula;
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteCursoAdicional(Long cursoAdicionalId) {
        cursoAdicionalCurriculaDAO.delete(new CursoAdicionalCurricula(cursoAdicionalId));
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteCursoOpcional(Long cursoOpcionalId) {
        cursoOpcionalCurriculaDAO.delete(new CursoOpcionalCurricula(cursoOpcionalId));
    }

    @Override
    public List<Curso> allCursosByCodigo(String codigo) {
        return cursoDAO.allByCodigo(codigo);
    }

}
