package pe.edu.lamolina.pivot.dao.finanzas.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.finanzas.ConceptoPrecioDAO;
import pe.edu.lamolina.pivot.model.finanzas.ConceptoPrecio;
import org.springframework.stereotype.Repository;

@Repository
public class ConceptoPrecioDAOH extends AbstractDAO<ConceptoPrecio> implements ConceptoPrecioDAO {

    public ConceptoPrecioDAOH() {
        super();
        setClazz(ConceptoPrecio.class);
    }
}

