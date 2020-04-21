package pe.edu.lamolina.amauta.dao.general.hibernate;

import pe.edu.lamolina.amauta.dao.general.CoordinadorAmbientesDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.CoordinadorAmbientes;

@Repository
public class CoordinadorAmbientesDAOH extends AbstractEasyDAO<CoordinadorAmbientes> implements CoordinadorAmbientesDAO {

    public CoordinadorAmbientesDAOH() {
        super();
        setClazz(CoordinadorAmbientes.class);
    }
}
