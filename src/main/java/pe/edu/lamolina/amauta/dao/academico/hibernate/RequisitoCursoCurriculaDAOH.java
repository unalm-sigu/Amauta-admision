package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import pe.edu.lamolina.amauta.dao.academico.RequisitoCursoCurriculaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.RequisitoCursoCurricula;
import static pe.edu.lamolina.model.enums.EstadoEnum.ACT;

@Repository
public class RequisitoCursoCurriculaDAOH extends AbstractEasyDAO<RequisitoCursoCurricula> implements RequisitoCursoCurriculaDAO {

    public RequisitoCursoCurriculaDAOH() {
        super();
        setClazz(RequisitoCursoCurricula.class);
    }

    @Override
    public List<RequisitoCursoCurricula> allByCursoCurricula(CursoCurricula cursoCurricula) {
        Octavia sql = Octavia.query()
                .from(RequisitoCursoCurricula.class, "rcc")
                .join("cursoCurricula cCur", "cursoRequisito cr", "cr.curso cur")
                .filter("estado", ACT)
                .filter("cCur.id", cursoCurricula);

        return all(sql);
    }

    @Override
    public List<RequisitoCursoCurricula> allByPlanCurricular(PlanCurricular plan) {
        Octavia sql = Octavia.query()
                .from(RequisitoCursoCurricula.class, "rcc")
                .join("cursoCurricula cCur", "cursoRequisito cr", "cCur.curso cur", "cr.curso", "cCur.planCurricular plan")
                .filter("estado", ACT)
                .filter("plan.id", plan);

        return all(sql);
    }

    @Override
    public List<RequisitoCursoCurricula> allByRequisitoCurriculaDe(CursoCurricula cursoCurricula) {
        Octavia sql = Octavia.query()
                .from(RequisitoCursoCurricula.class, "rcc")
                .join("cursoCurricula cCur", "cursoRequisito cr", "cCur.curso cur")
                .filter("estado", ACT)
                .filter("cr.id", cursoCurricula);

        return all(sql);
    }

    @Override
    public List<RequisitoCursoCurricula> allByCursosCurricula(List<CursoCurricula> cursosCurricula) {
        Octavia sql = Octavia.query()
                .from(RequisitoCursoCurricula.class, "rcc")
                .join("cursoCurricula cCur", "cursoRequisito cr", "cr.curso cur", "cr.tipoCursoCurricula")
                .filter("estado", ACT)
                .in("cCur.id", cursosCurricula);

        return all(sql);
    }

    @Override
    public List<RequisitoCursoCurricula> allPostRequisitosByCursosCurricula(List<CursoCurricula> cursosCurricula) {
        Octavia sql = Octavia.query()
                .from(RequisitoCursoCurricula.class, "rcc")
                .join("cursoCurricula cCur", "cursoRequisito cr", "cCur.curso cur", "cCur.tipoCursoCurricula")
                .filter("estado", ACT)
                .in("cr.id", cursosCurricula);

        return all(sql);
    }

    @Override
    public List<RequisitoCursoCurricula> allByPlanes(List<PlanCurricular> planes) {
        Octavia sql = Octavia.query()
                .from(RequisitoCursoCurricula.class, "rcc")
                .join("cursoCurricula cCur", "cursoRequisito cr", "cCur.curso cur", "cr.curso", "cCur.planCurricular plan")
                .filter("estado", ACT)
                .in("plan.id", planes);

        return all(sql);
    }

}
