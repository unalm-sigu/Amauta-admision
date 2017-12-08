package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.RequisitoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.model.academico.RequisitoCursoCurricula;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.pivot.model.academico.CursoCurricula;

@Repository
public class RequisitoCursoCurriculaDAOH extends AbstractDAO<RequisitoCursoCurricula> implements RequisitoCursoCurriculaDAO {

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
        return sql.all(getCurrentSession());
    }

    @Override
    public List<RequisitoCursoCurricula> allByRequisito(CursoCurricula cursoCurricula) {
        Octavia sql = Octavia.query()
                .from(RequisitoCursoCurricula.class, "rcc")
                .join("cursoCurricula cCur", "cursoRequisito cr", "cCur.curso cur")
                .filter("cr.id", cursoCurricula);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<RequisitoCursoCurricula> allByCursosCurricula(List<CursoCurricula> cursosCurricula) {
        Octavia sql = Octavia.query()
                .from(RequisitoCursoCurricula.class, "rcc")
                .join("cursoCurricula cCur", "cursoRequisito cr", "cr.curso cur", "cr.tipoCursoCurricula")
                .in("cCur.id", cursosCurricula);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<RequisitoCursoCurricula> allPostRequisitosByCursosCurricula(List<CursoCurricula> cursosCurricula) {
        Octavia sql = Octavia.query()
                .from(RequisitoCursoCurricula.class, "rcc")
                .join("cursoCurricula cCur", "cursoRequisito cr", "cCur.curso cur", "cCur.tipoCursoCurricula")
                .in("cr.id", cursosCurricula);
        return sql.all(getCurrentSession());
    }

}
