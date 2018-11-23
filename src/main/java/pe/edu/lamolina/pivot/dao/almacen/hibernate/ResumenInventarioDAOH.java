package pe.edu.lamolina.pivot.dao.almacen.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.almacen.ResumenInventario;
import pe.edu.lamolina.pivot.dao.almacen.ResumenInventarioDAO;

@Repository
public class ResumenInventarioDAOH extends AbstractEasyDAO<ResumenInventario> implements ResumenInventarioDAO {

    public ResumenInventarioDAOH() {
        super();
        setClazz(ResumenInventario.class);
    }

}
