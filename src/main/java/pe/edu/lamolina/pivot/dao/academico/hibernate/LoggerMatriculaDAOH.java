package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.LoggerMatriculaDAO;
import pe.edu.lamolina.pivot.model.academico.LoggerMatricula;
import org.springframework.stereotype.Repository;

@Repository
public class LoggerMatriculaDAOH extends AbstractDAO<LoggerMatricula> implements LoggerMatriculaDAO {

    public LoggerMatriculaDAOH() {
        super();
        setClazz(LoggerMatricula.class);
    }
}

