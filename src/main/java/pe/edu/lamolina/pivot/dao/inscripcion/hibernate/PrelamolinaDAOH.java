package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.PrelamolinaDAO;
import pe.edu.lamolina.pivot.model.inscripcion.Prelamolina;
import org.springframework.stereotype.Repository;

@Repository
public class PrelamolinaDAOH extends AbstractDAO<Prelamolina> implements PrelamolinaDAO {

    public PrelamolinaDAOH() {
        super();
        setClazz(Prelamolina.class);
    }
}

