package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.RequisitoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.model.academico.RequisitoCursoCurricula;
import org.springframework.stereotype.Repository;

@Repository
public class RequisitoCursoCurriculaDAOH extends AbstractDAO<RequisitoCursoCurricula> implements RequisitoCursoCurriculaDAO {

    public RequisitoCursoCurriculaDAOH() {
        super();
        setClazz(RequisitoCursoCurricula.class);
    }
}

