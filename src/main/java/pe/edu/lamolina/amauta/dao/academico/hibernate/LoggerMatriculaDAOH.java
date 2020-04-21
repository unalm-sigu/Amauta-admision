package pe.edu.lamolina.amauta.dao.academico.hibernate;

import pe.edu.lamolina.amauta.dao.academico.LoggerMatriculaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.LoggerMatricula;

@Repository
public class LoggerMatriculaDAOH extends AbstractEasyDAO<LoggerMatricula> implements LoggerMatriculaDAO {

    public LoggerMatriculaDAOH() {
        super();
        setClazz(LoggerMatricula.class);
    }
}
