package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.pivot.model.tramite.TipoTramite;
import org.springframework.stereotype.Repository;

@Repository
public class TipoTramiteDAOH extends AbstractDAO<TipoTramite> implements TipoTramiteDAO {

    public TipoTramiteDAOH() {
        super();
        setClazz(TipoTramite.class);
    }
}

