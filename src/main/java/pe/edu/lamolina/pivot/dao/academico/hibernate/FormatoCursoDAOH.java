package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.FormatoCursoDAO;
import pe.edu.lamolina.pivot.model.academico.FormatoCurso;
import org.springframework.stereotype.Repository;

@Repository
public class FormatoCursoDAOH extends AbstractDAO<FormatoCurso> implements FormatoCursoDAO {

    public FormatoCursoDAOH() {
        super();
        setClazz(FormatoCurso.class);
    }
}

