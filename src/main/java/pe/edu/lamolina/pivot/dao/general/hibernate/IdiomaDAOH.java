package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.IdiomaDAO;
import pe.edu.lamolina.pivot.model.general.Idioma;
import org.springframework.stereotype.Repository;

@Repository
public class IdiomaDAOH extends AbstractDAO<Idioma> implements IdiomaDAO {

    public IdiomaDAOH() {
        super();
        setClazz(Idioma.class);
    }
}

