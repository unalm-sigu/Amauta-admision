package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.edu.lamolina.pivot.dao.general.CompaniaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Compania;

@Repository
public class CompaniaDAOH extends AbstractEasyDAO<Compania> implements CompaniaDAO {

    public CompaniaDAOH() {
        super();
        setClazz(Compania.class);
    }
}
