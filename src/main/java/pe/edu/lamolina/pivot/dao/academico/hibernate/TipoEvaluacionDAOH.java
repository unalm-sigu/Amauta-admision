package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoEvaluacionDAO;
import pe.edu.lamolina.pivot.model.academico.TipoEvaluacion;
import org.springframework.stereotype.Repository;

@Repository
public class TipoEvaluacionDAOH extends AbstractDAO<TipoEvaluacion> implements TipoEvaluacionDAO {

    public TipoEvaluacionDAOH() {
        super();
        setClazz(TipoEvaluacion.class);
    }
}

