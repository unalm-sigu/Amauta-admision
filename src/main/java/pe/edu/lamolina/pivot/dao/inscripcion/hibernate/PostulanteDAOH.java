package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.PostulanteDAO;
import pe.edu.lamolina.pivot.model.inscripcion.Postulante;
import org.springframework.stereotype.Repository;

@Repository
public class PostulanteDAOH extends AbstractDAO<Postulante> implements PostulanteDAO {

    public PostulanteDAOH() {
        super();
        setClazz(Postulante.class);
    }
}

