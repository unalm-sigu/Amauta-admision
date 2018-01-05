package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.inscripcion.PostulanteDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Repository
public class PostulanteDAOH extends AbstractEasyDAO<Postulante> implements PostulanteDAO {

    public PostulanteDAOH() {
        super();
        setClazz(Postulante.class);
    }

    @Override
    public List<Postulante> allByPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Postulante.class, "pos")
                .join("persona per")
                .filter("per.id", persona);

        return all(sql);
    }
}
