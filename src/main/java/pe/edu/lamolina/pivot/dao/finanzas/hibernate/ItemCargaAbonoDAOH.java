package pe.edu.lamolina.pivot.dao.finanzas.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.finanzas.ItemCargaAbonoDAO;
import pe.edu.lamolina.pivot.model.finanzas.ItemCargaAbono;
import org.springframework.stereotype.Repository;

@Repository
public class ItemCargaAbonoDAOH extends AbstractDAO<ItemCargaAbono> implements ItemCargaAbonoDAO {

    public ItemCargaAbonoDAOH() {
        super();
        setClazz(ItemCargaAbono.class);
    }
}

