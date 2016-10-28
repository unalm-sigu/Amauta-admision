package pe.edu.lamolina.pivot.dao.finanzas.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.finanzas.FacturaDAO;
import pe.edu.lamolina.pivot.model.finanzas.Factura;
import org.springframework.stereotype.Repository;

@Repository
public class FacturaDAOH extends AbstractDAO<Factura> implements FacturaDAO {

    public FacturaDAOH() {
        super();
        setClazz(Factura.class);
    }
}

