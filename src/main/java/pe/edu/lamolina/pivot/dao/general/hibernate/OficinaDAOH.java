package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.model.general.Oficina;
import org.springframework.stereotype.Repository;

@Repository
public class OficinaDAOH extends AbstractDAO<Oficina> implements OficinaDAO {

    public OficinaDAOH() {
        super();
        setClazz(Oficina.class);
    }
}

