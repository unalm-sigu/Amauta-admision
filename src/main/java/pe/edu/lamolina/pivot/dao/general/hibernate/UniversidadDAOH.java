package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.UniversidadDAO;
import pe.edu.lamolina.pivot.model.general.Universidad;
import org.springframework.stereotype.Repository;

@Repository
public class UniversidadDAOH extends AbstractDAO<Universidad> implements UniversidadDAO {

    public UniversidadDAOH() {
        super();
        setClazz(Universidad.class);
    }
}

