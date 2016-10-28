package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoEvaluacionDAO;
import pe.edu.lamolina.pivot.model.academico.AlumnoEvaluacion;
import org.springframework.stereotype.Repository;

@Repository
public class AlumnoEvaluacionDAOH extends AbstractDAO<AlumnoEvaluacion> implements AlumnoEvaluacionDAO {

    public AlumnoEvaluacionDAOH() {
        super();
        setClazz(AlumnoEvaluacion.class);
    }
}

