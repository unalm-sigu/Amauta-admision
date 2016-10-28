package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.IngresanteDAO;
import pe.edu.lamolina.pivot.model.inscripcion.Ingresante;
import org.springframework.stereotype.Repository;

@Repository
public class IngresanteDAOH extends AbstractDAO<Ingresante> implements IngresanteDAO {

    public IngresanteDAOH() {
        super();
        setClazz(Ingresante.class);
    }
}

