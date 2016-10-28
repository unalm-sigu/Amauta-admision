package pe.edu.lamolina.pivot.dao.finanzas.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.finanzas.ItemFacturaDAO;
import pe.edu.lamolina.pivot.model.finanzas.ItemFactura;
import org.springframework.stereotype.Repository;

@Repository
public class ItemFacturaDAOH extends AbstractDAO<ItemFactura> implements ItemFacturaDAO {

    public ItemFacturaDAOH() {
        super();
        setClazz(ItemFactura.class);
    }
}

