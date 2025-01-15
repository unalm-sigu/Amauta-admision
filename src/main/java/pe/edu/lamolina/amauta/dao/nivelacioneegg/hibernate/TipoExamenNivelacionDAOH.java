package pe.edu.lamolina.amauta.dao.nivelacioneegg.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.TipoExamenNivelacionDAO;
import pe.edu.lamolina.model.nivelacioneegg.TipoExamenNivelacion;

@Repository
public class TipoExamenNivelacionDAOH extends AbstractEasyDAO<TipoExamenNivelacion> implements TipoExamenNivelacionDAO {

    public TipoExamenNivelacionDAOH() {
        super();
        setClazz(TipoExamenNivelacion.class);
    }

}
