package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import pe.edu.lamolina.pivot.dao.tramite.TipoTramiteDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.TipoTramite;

@Repository
public class TipoTramiteDAOH extends AbstractEasyDAO<TipoTramite> implements TipoTramiteDAO {

    public TipoTramiteDAOH() {
        super();
        setClazz(TipoTramite.class);
    }
}
