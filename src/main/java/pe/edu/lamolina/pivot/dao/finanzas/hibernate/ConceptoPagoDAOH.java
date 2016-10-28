package pe.edu.lamolina.pivot.dao.finanzas.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.finanzas.ConceptoPagoDAO;
import pe.edu.lamolina.pivot.model.finanzas.ConceptoPago;
import org.springframework.stereotype.Repository;

@Repository
public class ConceptoPagoDAOH extends AbstractDAO<ConceptoPago> implements ConceptoPagoDAO {

    public ConceptoPagoDAOH() {
        super();
        setClazz(ConceptoPago.class);
    }
}

