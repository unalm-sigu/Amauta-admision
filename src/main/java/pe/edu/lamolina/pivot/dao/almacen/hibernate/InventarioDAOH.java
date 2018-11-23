package pe.edu.lamolina.pivot.dao.almacen.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.almacen.Inventario;
import pe.edu.lamolina.pivot.dao.almacen.InventarioDAO;

@Repository
public class InventarioDAOH extends AbstractEasyDAO<Inventario> implements InventarioDAO {

    public InventarioDAOH() {
        super();
        setClazz(Inventario.class);
    }

}
