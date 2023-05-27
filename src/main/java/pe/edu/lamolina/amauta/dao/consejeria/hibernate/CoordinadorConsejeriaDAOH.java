package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.consejeria.CoordinadorConsejeria;
import pe.edu.lamolina.amauta.dao.consejeria.CoordinadorConsejeriaDAO;

@Repository
public class CoordinadorConsejeriaDAOH extends AbstractEasyDAO<CoordinadorConsejeria> implements CoordinadorConsejeriaDAO {

    public CoordinadorConsejeriaDAOH() {
        super();
        setClazz(CoordinadorConsejeria.class);
    }

}
