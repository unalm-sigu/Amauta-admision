package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.pivot.model.general.TipoDocIdentidad;
import org.springframework.stereotype.Repository;

@Repository
public class TipoDocIdentidadDAOH extends AbstractDAO<TipoDocIdentidad> implements TipoDocIdentidadDAO {

    public TipoDocIdentidadDAOH() {
        super();
        setClazz(TipoDocIdentidad.class);
    }
}

