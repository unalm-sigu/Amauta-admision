package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.model.academico.MatriculaResumen;
import org.springframework.stereotype.Repository;

@Repository
public class MatriculaResumenDAOH extends AbstractDAO<MatriculaResumen> implements MatriculaResumenDAO {

    public MatriculaResumenDAOH() {
        super();
        setClazz(MatriculaResumen.class);
    }
}

