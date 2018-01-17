package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.edu.lamolina.pivot.dao.general.TipoUbicacionDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.TipoUbicacion;

@Repository
public class TipoUbicacionDAOH extends AbstractEasyDAO<TipoUbicacion> implements TipoUbicacionDAO {

    public TipoUbicacionDAOH() {
        super();
        setClazz(TipoUbicacion.class);
    }
}
