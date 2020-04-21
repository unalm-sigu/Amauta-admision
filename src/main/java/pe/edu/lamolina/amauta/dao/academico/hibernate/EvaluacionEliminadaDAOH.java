package pe.edu.lamolina.amauta.dao.academico.hibernate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.EvaluacionEliminada;
import pe.edu.lamolina.amauta.dao.academico.EvaluacionEliminadaDAO;

@Repository
public class EvaluacionEliminadaDAOH extends AbstractEasyDAO<EvaluacionEliminada> implements EvaluacionEliminadaDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public EvaluacionEliminadaDAOH() {
        super();
        setClazz(EvaluacionEliminada.class);
    }

}
