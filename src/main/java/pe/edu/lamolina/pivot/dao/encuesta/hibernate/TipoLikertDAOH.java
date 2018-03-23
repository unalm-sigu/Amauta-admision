package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.encuesta.TipoLikert;
import pe.edu.lamolina.pivot.dao.encuesta.TipoLikertDAO;

@Repository
public class TipoLikertDAOH extends AbstractEasyDAO<TipoLikert> implements TipoLikertDAO {

    public TipoLikertDAOH() {
        super();
        setClazz(TipoLikert.class);
    }

}
