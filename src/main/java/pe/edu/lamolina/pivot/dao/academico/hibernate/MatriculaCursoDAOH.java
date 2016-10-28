package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.model.academico.MatriculaCurso;
import org.springframework.stereotype.Repository;

@Repository
public class MatriculaCursoDAOH extends AbstractDAO<MatriculaCurso> implements MatriculaCursoDAO {

    public MatriculaCursoDAOH() {
        super();
        setClazz(MatriculaCurso.class);
    }
}

