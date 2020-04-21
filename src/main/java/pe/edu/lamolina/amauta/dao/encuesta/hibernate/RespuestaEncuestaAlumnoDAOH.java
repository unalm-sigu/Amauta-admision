package pe.edu.lamolina.amauta.dao.encuesta.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.RespuestaEncuestaAlumno;
import pe.edu.lamolina.amauta.dao.encuesta.RespuestaEncuestaAlumnoDAO;

@Repository
public class RespuestaEncuestaAlumnoDAOH extends AbstractEasyDAO<RespuestaEncuestaAlumno> implements RespuestaEncuestaAlumnoDAO {

    public RespuestaEncuestaAlumnoDAOH() {
        super();
        setClazz(RespuestaEncuestaAlumno.class);
    }

    @Override
    public List<RespuestaEncuestaAlumno> allComentariosByEncuestaDocente(EncuestaDocente encuestaDocente) {
        Octavia sql = Octavia.query(RespuestaEncuestaAlumno.class, "rea")
                .join("encuestaDocente ed")
                .filter("ed.id", encuestaDocente)
                .isNotNull("rea.comentario");
        
        return all(sql);
    }

}
