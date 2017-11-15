package pe.edu.lamolina.pivot.controller.academico.plancurricular;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoAdicionalCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.OrientacionCarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCurricularDAO;
import pe.edu.lamolina.pivot.dao.academico.RequisitoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.CursoAdicionalCurricula;
import pe.edu.lamolina.pivot.model.academico.CursoCurricula;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.academico.OrientacionCarrera;
import pe.edu.lamolina.pivot.model.academico.PlanCurricular;
import pe.edu.lamolina.pivot.model.academico.RequisitoCursoCurricula;
import pe.edu.lamolina.pivot.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

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
        planCurricular.setEstadoEnum(EstadoEnum.ACT);
        planCurricularDAO.save(planCurricular);
        return planCurricular;
    }

    @Override
    @Transactional(readOnly = false)
    public void agregarCursoCurricula(CursoCurricula cursoCurricula) {
        if (cursoCurricula.getRequisitosCurricula() != null && !cursoCurricula.getRequisitosCurricula().isEmpty()) {
            for (RequisitoCursoCurricula reqCurricula : cursoCurricula.getRequisitosCurricula()) {
                reqCurricula.setCursoCurricula(cursoCurricula);
            }
        }
        cursoCurriculaDAO.save(cursoCurricula);
    }

    @Override
    @Transactional(readOnly = false)
    public void agregarCursoAdcCurricula(CursoAdicionalCurricula cursoAdicionalCurricula) {
        cursoAdicionalCurriculaDAO.save(cursoAdicionalCurricula);
    }

    @Override
    @Transactional(readOnly = false)
    public void updateCursoCurricula(CursoCurricula cursoCurricula) {
        CursoCurricula cursoCurriculaDB = cursoCurriculaDAO.find(cursoCurricula.getId());
        List<RequisitoCursoCurricula> requisitosDB = requisitoCursoCurriculaDAO.allByCursoCurricula(cursoCurricula);

        cursoCurriculaDAO.updateCreditoRequisito(cursoCurricula);

        for (RequisitoCursoCurricula requisitoDB : requisitosDB) {

            RequisitoCursoCurricula requisitoFound = cursoCurricula.getRequisitosCurricula().stream().filter(req -> requisitoDB.getId().equals(req.getId())).findFirst().orElse(null);
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
    public List<PlanCurricular> allByDynatable(DynatableFilter filter, Facultad facultad) {
        return planCurricularDAO.allByDynatable(filter, facultad);
    }

    @Override
    public List<CursoCurricula> allCursosOblByDynatable(DynatableFilter filter) {
        if (filter.getQueries() == null) {
            new ArrayList<CursoCurricula>();
        }
        return cursoCurriculaDAO.allByDynatable(filter);
    }

    @Override
    public List<CursoAdicionalCurricula> allCursosAdcByDynatable(DynatableFilter filter) {
        if (filter.getQueries() == null) {
            new ArrayList<CursoCurricula>();
        }
        return cursoAdicionalCurriculaDAO.allByDynatable(filter);
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
    public List<Curso> allCursoByNombre(String nombre) {
        return cursoDAO.allByNombreFilter(nombre, 10);
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

}
