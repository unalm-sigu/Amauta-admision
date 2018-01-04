package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.PostulanteDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Repository
public class PostulanteDAOH extends AbstractDAO<Postulante> implements PostulanteDAO {

    public PostulanteDAOH() {
        super();
        setClazz(Postulante.class);
    }

    @Override
    public List<Postulante> allByPersona(Persona persona) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pos")
                .parents("persona per")
                .filter("per.id", persona);
        return this.all(sqlUtil);
    }
}
