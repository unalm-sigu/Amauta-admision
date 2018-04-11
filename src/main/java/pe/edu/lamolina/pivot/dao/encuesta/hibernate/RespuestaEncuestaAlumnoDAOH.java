package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.encuestaestudiantil.RespuestaEncuestaAlumno;
import pe.edu.lamolina.pivot.dao.encuesta.RespuestaEncuestaAlumnoDAO;

@Repository
public class RespuestaEncuestaAlumnoDAOH extends AbstractEasyDAO<RespuestaEncuestaAlumno> implements RespuestaEncuestaAlumnoDAO {

    public RespuestaEncuestaAlumnoDAOH() {
        super();
        setClazz(RespuestaEncuestaAlumno.class);
    }

}
