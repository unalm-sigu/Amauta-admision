package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCalificacionDAO;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import org.springframework.stereotype.Repository;

@Repository
public class PlanCalificacionDAOH extends AbstractDAO<PlanCalificacion> implements PlanCalificacionDAO {

    public PlanCalificacionDAOH() {
        super();
        setClazz(PlanCalificacion.class);
    }
}

