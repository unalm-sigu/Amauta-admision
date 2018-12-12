package pe.edu.lamolina.pivot.dao.academico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.TipoActividadIngresante;
import pe.edu.lamolina.pivot.dao.academico.TipoActividadIngresanteDAO;

@Repository
public class TipoActividadIngresanteDAOH extends AbstractEasyDAO<TipoActividadIngresante> implements TipoActividadIngresanteDAO {

    public TipoActividadIngresanteDAOH() {
        super();
        setClazz(TipoActividadIngresante.class);
    }

}
