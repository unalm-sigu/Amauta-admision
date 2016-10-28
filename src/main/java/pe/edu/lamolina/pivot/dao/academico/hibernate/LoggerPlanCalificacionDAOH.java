package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.LoggerPlanCalificacionDAO;
import pe.edu.lamolina.pivot.model.academico.LoggerPlanCalificacion;
import org.springframework.stereotype.Repository;

@Repository
public class LoggerPlanCalificacionDAOH extends AbstractDAO<LoggerPlanCalificacion> implements LoggerPlanCalificacionDAO {

    public LoggerPlanCalificacionDAOH() {
        super();
        setClazz(LoggerPlanCalificacion.class);
    }
}

