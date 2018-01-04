package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.edu.lamolina.pivot.dao.academico.TipoCursoCurriculaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;

@Repository
public class TipoCursoCurriculaDAOH extends AbstractEasyDAO<TipoCursoCurricula> implements TipoCursoCurriculaDAO {

    public TipoCursoCurriculaDAOH() {
        super();
        setClazz(TipoCursoCurricula.class);
    }
}
