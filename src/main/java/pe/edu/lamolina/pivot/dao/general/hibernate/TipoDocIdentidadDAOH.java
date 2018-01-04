package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.TipoDocIdentidad;

@Repository
public class TipoDocIdentidadDAOH extends AbstractEasyDAO<TipoDocIdentidad> implements TipoDocIdentidadDAO {

    public TipoDocIdentidadDAOH() {
        super();
        setClazz(TipoDocIdentidad.class);
    }
}
