package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.model.academico.Curso;
import org.springframework.stereotype.Repository;

@Repository
public class CursoDAOH extends AbstractDAO<Curso> implements CursoDAO {

    public CursoDAOH() {
        super();
        setClazz(Curso.class);
    }
}

