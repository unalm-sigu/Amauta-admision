package pe.edu.lamolina.pivot.dao.academico.hibernate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.model.academico.EvaluacionEliminada;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionEliminadaDAO;

@Repository
public class EvaluacionEliminadaDAOH extends AbstractDAO<EvaluacionEliminada> implements EvaluacionEliminadaDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public EvaluacionEliminadaDAOH() {
        super();
        setClazz(EvaluacionEliminada.class);
    }

}
