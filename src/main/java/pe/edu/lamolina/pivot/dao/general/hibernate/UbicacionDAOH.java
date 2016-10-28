package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.UbicacionDAO;
import pe.edu.lamolina.pivot.model.general.Ubicacion;
import org.springframework.stereotype.Repository;

@Repository
public class UbicacionDAOH extends AbstractDAO<Ubicacion> implements UbicacionDAO {

    public UbicacionDAOH() {
        super();
        setClazz(Ubicacion.class);
    }
}

