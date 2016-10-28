package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.PaisDAO;
import pe.edu.lamolina.pivot.model.general.Pais;
import org.springframework.stereotype.Repository;

@Repository
public class PaisDAOH extends AbstractDAO<Pais> implements PaisDAO {

    public PaisDAOH() {
        super();
        setClazz(Pais.class);
    }
}

