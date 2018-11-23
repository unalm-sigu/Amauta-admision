package pe.edu.lamolina.pivot.dao.almacen.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.almacen.Almacen;
import pe.edu.lamolina.pivot.dao.almacen.AlmacenDAO;

@Repository
public class AlmacenDAOH extends AbstractEasyDAO<Almacen> implements AlmacenDAO {

    public AlmacenDAOH() {
        super();
        setClazz(Almacen.class);
    }

}
