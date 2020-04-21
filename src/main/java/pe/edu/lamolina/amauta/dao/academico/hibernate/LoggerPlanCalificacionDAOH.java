package pe.edu.lamolina.amauta.dao.academico.hibernate;

import pe.edu.lamolina.amauta.dao.academico.LoggerPlanCalificacionDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.LoggerPlanCalificacion;

@Repository
public class LoggerPlanCalificacionDAOH extends AbstractEasyDAO<LoggerPlanCalificacion> implements LoggerPlanCalificacionDAO {

    public LoggerPlanCalificacionDAOH() {
        super();
        setClazz(LoggerPlanCalificacion.class);
    }
}
