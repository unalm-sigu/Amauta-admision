package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.TipoAulaDAO;
import pe.edu.lamolina.pivot.model.general.TipoAula;
import org.springframework.stereotype.Repository;

@Repository
public class TipoAulaDAOH extends AbstractDAO<TipoAula> implements TipoAulaDAO {

    public TipoAulaDAOH() {
        super();
        setClazz(TipoAula.class);
    }
}

