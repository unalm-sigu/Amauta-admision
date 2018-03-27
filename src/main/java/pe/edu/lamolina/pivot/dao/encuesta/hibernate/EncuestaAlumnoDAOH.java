package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.encuesta.EncuestaAlumno;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaAlumnoDAO;

@Repository
public class EncuestaAlumnoDAOH extends AbstractEasyDAO<EncuestaAlumno> implements EncuestaAlumnoDAO {

    public EncuestaAlumnoDAOH() {
        super();
        setClazz(EncuestaAlumno.class);
    }

}
