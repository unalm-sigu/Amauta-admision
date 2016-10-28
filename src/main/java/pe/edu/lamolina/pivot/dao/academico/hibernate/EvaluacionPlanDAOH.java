package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionPlanDAO;
import pe.edu.lamolina.pivot.model.academico.EvaluacionPlan;
import org.springframework.stereotype.Repository;

@Repository
public class EvaluacionPlanDAOH extends AbstractDAO<EvaluacionPlan> implements EvaluacionPlanDAO {

    public EvaluacionPlanDAOH() {
        super();
        setClazz(EvaluacionPlan.class);
    }
}

