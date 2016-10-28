package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.model.academico.TipoCursoCurricula;
import org.springframework.stereotype.Repository;

@Repository
public class TipoCursoCurriculaDAOH extends AbstractDAO<TipoCursoCurricula> implements TipoCursoCurriculaDAO {

    public TipoCursoCurriculaDAOH() {
        super();
        setClazz(TipoCursoCurricula.class);
    }
}

