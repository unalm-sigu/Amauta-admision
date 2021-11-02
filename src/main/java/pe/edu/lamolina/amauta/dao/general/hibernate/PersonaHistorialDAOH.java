package pe.edu.lamolina.amauta.dao.general.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaHistorialDAO;
import pe.edu.lamolina.model.general.PersonaHistorial;

@Repository
public class PersonaHistorialDAOH extends AbstractEasyDAO<PersonaHistorial> implements PersonaHistorialDAO {

    public PersonaHistorialDAOH() {
        super();
        setClazz(PersonaHistorial.class);
    }

}
