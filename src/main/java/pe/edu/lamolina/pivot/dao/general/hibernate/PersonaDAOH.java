package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.model.general.Persona;
import org.springframework.stereotype.Repository;

@Repository
public class PersonaDAOH extends AbstractDAO<Persona> implements PersonaDAO {

    public PersonaDAOH() {
        super();
        setClazz(Persona.class);
    }
}

