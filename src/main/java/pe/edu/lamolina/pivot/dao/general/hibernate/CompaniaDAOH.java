package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.CompaniaDAO;
import pe.edu.lamolina.pivot.model.general.Compania;
import org.springframework.stereotype.Repository;

@Repository
public class CompaniaDAOH extends AbstractDAO<Compania> implements CompaniaDAO {

    public CompaniaDAOH() {
        super();
        setClazz(Compania.class);
    }
}

