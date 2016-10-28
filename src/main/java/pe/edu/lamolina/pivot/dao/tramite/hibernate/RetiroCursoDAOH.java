package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.tramite.RetiroCursoDAO;
import pe.edu.lamolina.pivot.model.tramite.RetiroCurso;
import org.springframework.stereotype.Repository;

@Repository
public class RetiroCursoDAOH extends AbstractDAO<RetiroCurso> implements RetiroCursoDAO {

    public RetiroCursoDAOH() {
        super();
        setClazz(RetiroCurso.class);
    }
}

