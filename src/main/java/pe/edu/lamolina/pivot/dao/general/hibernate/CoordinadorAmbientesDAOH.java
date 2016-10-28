package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.CoordinadorAmbientesDAO;
import pe.edu.lamolina.pivot.model.general.CoordinadorAmbientes;
import org.springframework.stereotype.Repository;

@Repository
public class CoordinadorAmbientesDAOH extends AbstractDAO<CoordinadorAmbientes> implements CoordinadorAmbientesDAO {

    public CoordinadorAmbientesDAOH() {
        super();
        setClazz(CoordinadorAmbientes.class);
    }
}

