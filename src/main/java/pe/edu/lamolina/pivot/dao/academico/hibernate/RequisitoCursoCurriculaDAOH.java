package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.academico.RequisitoCursoCurriculaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.RequisitoCursoCurricula;

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
                .filter("cCur.id", cursoCurricula);

        return all(sql);
    }

    @Override
    public List<RequisitoCursoCurricula> allByPlanCurricular(PlanCurricular plan) {
        Octavia sql = Octavia.query()
                .from(RequisitoCursoCurricula.class, "rcc")
                .join("cursoCurricula cCur", "cursoRequisito cr", "cCur.curso cur", "cr.curso", "cCur.planCurricular")
                .filter("cCur.planCurricular", plan);

        return all(sql);
    }

    @Override
    public List<RequisitoCursoCurricula> allByRequisito(CursoCurricula cursoCurricula) {
        Octavia sql = Octavia.query()
                .from(RequisitoCursoCurricula.class, "rcc")
                .join("cursoCurricula cCur", "cursoRequisito cr", "cCur.curso cur")
                .filter("cr.id", cursoCurricula);

        return all(sql);
    }

    @Override
    public List<RequisitoCursoCurricula> allByCursosCurricula(List<CursoCurricula> cursosCurricula) {
        Octavia sql = Octavia.query()
                .from(RequisitoCursoCurricula.class, "rcc")
                .join("cursoCurricula cCur", "cursoRequisito cr", "cr.curso cur", "cr.tipoCursoCurricula")
                .in("cCur.id", cursosCurricula);

        return all(sql);
    }

    @Override
    public List<RequisitoCursoCurricula> allPostRequisitosByCursosCurricula(List<CursoCurricula> cursosCurricula) {
        Octavia sql = Octavia.query()
                .from(RequisitoCursoCurricula.class, "rcc")
                .join("cursoCurricula cCur", "cursoRequisito cr", "cCur.curso cur", "cCur.tipoCursoCurricula")
                .in("cr.id", cursosCurricula);

        return all(sql);
    }

}
