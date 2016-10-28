package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.ResumenPlanCurricularDAO;
import pe.edu.lamolina.pivot.model.academico.ResumenPlanCurricular;
import org.springframework.stereotype.Repository;

@Repository
public class ResumenPlanCurricularDAOH extends AbstractDAO<ResumenPlanCurricular> implements ResumenPlanCurricularDAO {

    public ResumenPlanCurricularDAOH() {
        super();
        setClazz(ResumenPlanCurricular.class);
    }
}

