package pe.edu.lamolina.amauta.dao.bienestar.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.bienestar.TipoSubvencionDAO;
import pe.edu.lamolina.model.bienestar.TipoSubvencion;

@Repository
public class TipoSubvencionDAOH extends AbstractEasyDAO<TipoSubvencion> implements TipoSubvencionDAO {

    public TipoSubvencionDAOH() {
        super();
        setClazz(TipoSubvencion.class);
    }
}
