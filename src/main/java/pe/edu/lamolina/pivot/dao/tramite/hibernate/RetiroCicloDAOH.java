package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import pe.edu.lamolina.pivot.dao.tramite.RetiroCicloDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.RetiroCiclo;

@Repository
public class RetiroCicloDAOH extends AbstractEasyDAO<RetiroCiclo> implements RetiroCicloDAO {

    public RetiroCicloDAOH() {
        super();
        setClazz(RetiroCiclo.class);
    }
}
