package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.NombreCursoDAO;
import pe.edu.lamolina.pivot.model.academico.NombreCurso;
import org.springframework.stereotype.Repository;

@Repository
public class NombreCursoDAOH extends AbstractDAO<NombreCurso> implements NombreCursoDAO {

    public NombreCursoDAOH() {
        super();
        setClazz(NombreCurso.class);
    }
}

