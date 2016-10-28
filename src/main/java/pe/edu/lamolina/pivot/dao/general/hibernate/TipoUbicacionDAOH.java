package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.TipoUbicacionDAO;
import pe.edu.lamolina.pivot.model.general.TipoUbicacion;
import org.springframework.stereotype.Repository;

@Repository
public class TipoUbicacionDAOH extends AbstractDAO<TipoUbicacion> implements TipoUbicacionDAO {

    public TipoUbicacionDAOH() {
        super();
        setClazz(TipoUbicacion.class);
    }
}

