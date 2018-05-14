package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.TipoResolucion;
import pe.edu.lamolina.pivot.dao.tramite.TipoResolucionDAO;

@Repository
public class TipoResolucionDAOH extends AbstractEasyDAO<TipoResolucion> implements TipoResolucionDAO {

    public TipoResolucionDAOH() {
        super();
        setClazz(TipoResolucion.class);
    }

}
