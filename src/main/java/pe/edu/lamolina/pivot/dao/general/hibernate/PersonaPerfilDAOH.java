package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaPerfilDAO;
import pe.edu.lamolina.pivot.model.general.PersonaPerfil;
import org.springframework.stereotype.Repository;

@Repository
public class PersonaPerfilDAOH extends AbstractDAO<PersonaPerfil> implements PersonaPerfilDAO {

    public PersonaPerfilDAOH() {
        super();
        setClazz(PersonaPerfil.class);
    }
}

