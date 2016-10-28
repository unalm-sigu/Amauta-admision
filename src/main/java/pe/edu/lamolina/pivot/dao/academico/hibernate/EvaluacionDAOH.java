package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionDAO;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;
import org.springframework.stereotype.Repository;

@Repository
public class EvaluacionDAOH extends AbstractDAO<Evaluacion> implements EvaluacionDAO {

    public EvaluacionDAOH() {
        super();
        setClazz(Evaluacion.class);
    }
}

