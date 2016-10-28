package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.EvaluadoDAO;
import pe.edu.lamolina.pivot.model.inscripcion.Evaluado;
import org.springframework.stereotype.Repository;

@Repository
public class EvaluadoDAOH extends AbstractDAO<Evaluado> implements EvaluadoDAO {

    public EvaluadoDAOH() {
        super();
        setClazz(Evaluado.class);
    }
}

