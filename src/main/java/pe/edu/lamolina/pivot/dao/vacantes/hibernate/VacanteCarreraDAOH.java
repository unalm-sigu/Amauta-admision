package pe.edu.lamolina.pivot.dao.vacantes.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.vacantes.VacanteCarreraDAO;
import pe.edu.lamolina.pivot.model.vacantes.VacanteCarrera;
import org.springframework.stereotype.Repository;

@Repository
public class VacanteCarreraDAOH extends AbstractDAO<VacanteCarrera> implements VacanteCarreraDAO {

    public VacanteCarreraDAOH() {
        super();
        setClazz(VacanteCarrera.class);
    }
}

