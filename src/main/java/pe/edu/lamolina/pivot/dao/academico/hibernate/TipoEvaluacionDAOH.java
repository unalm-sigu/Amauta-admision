package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.edu.lamolina.pivot.dao.academico.TipoEvaluacionDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.TipoEvaluacion;

@Repository
public class TipoEvaluacionDAOH extends AbstractEasyDAO<TipoEvaluacion> implements TipoEvaluacionDAO {

    public TipoEvaluacionDAOH() {
        super();
        setClazz(TipoEvaluacion.class);
    }
}
