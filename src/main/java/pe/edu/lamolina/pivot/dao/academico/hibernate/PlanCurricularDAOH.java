package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCurricularDAO;
import pe.edu.lamolina.pivot.model.academico.PlanCurricular;
import org.springframework.stereotype.Repository;

@Repository
public class PlanCurricularDAOH extends AbstractDAO<PlanCurricular> implements PlanCurricularDAO {

    public PlanCurricularDAOH() {
        super();
        setClazz(PlanCurricular.class);
    }
}

