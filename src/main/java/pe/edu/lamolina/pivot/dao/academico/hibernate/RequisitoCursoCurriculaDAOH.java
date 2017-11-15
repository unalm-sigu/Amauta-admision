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
                .filter("cCur.id", cursoCurricula.getId());
        return sql.all(getCurrentSession());
    }
}
