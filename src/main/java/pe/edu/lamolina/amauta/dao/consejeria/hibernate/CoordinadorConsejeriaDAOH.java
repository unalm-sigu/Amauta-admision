package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import org.springframework.stereotype.Service;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.consejeria.CoordinadorConsejeria;
import pe.edu.lamolina.amauta.dao.consejeria.CoordinadorConsejeriaDAO;

@Service
public class CoordinadorConsejeriaDAOH extends AbstractEasyDAO<CoordinadorConsejeria> implements CoordinadorConsejeriaDAO {

    public CoordinadorConsejeriaDAOH() {
        super();
        setClazz(CoordinadorConsejeria.class);
    }

}
