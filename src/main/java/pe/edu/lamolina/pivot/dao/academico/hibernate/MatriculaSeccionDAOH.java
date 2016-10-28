package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.model.academico.MatriculaSeccion;
import org.springframework.stereotype.Repository;

@Repository
public class MatriculaSeccionDAOH extends AbstractDAO<MatriculaSeccion> implements MatriculaSeccionDAO {

    public MatriculaSeccionDAOH() {
        super();
        setClazz(MatriculaSeccion.class);
    }
}

