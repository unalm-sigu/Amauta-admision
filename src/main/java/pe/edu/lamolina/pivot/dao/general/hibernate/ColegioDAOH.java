package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.edu.lamolina.pivot.dao.general.ColegioDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Colegio;

@Repository
public class ColegioDAOH extends AbstractEasyDAO<Colegio> implements ColegioDAO {

    public ColegioDAOH() {
        super();
        setClazz(Colegio.class);
    }
}
