package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.PerfilCompaniaDAO;
import pe.edu.lamolina.pivot.model.general.PerfilCompania;
import org.springframework.stereotype.Repository;

@Repository
public class PerfilCompaniaDAOH extends AbstractDAO<PerfilCompania> implements PerfilCompaniaDAO {

    public PerfilCompaniaDAOH() {
        super();
        setClazz(PerfilCompania.class);
    }
}

