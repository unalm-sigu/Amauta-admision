package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionSeccionDAO;
import pe.edu.lamolina.pivot.model.academico.EvaluacionSeccion;
import org.springframework.stereotype.Repository;

@Repository
public class EvaluacionSeccionDAOH extends AbstractDAO<EvaluacionSeccion> implements EvaluacionSeccionDAO {

    public EvaluacionSeccionDAOH() {
        super();
        setClazz(EvaluacionSeccion.class);
    }
}

