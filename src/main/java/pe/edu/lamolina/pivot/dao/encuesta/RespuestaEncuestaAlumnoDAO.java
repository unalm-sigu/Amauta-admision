package pe.edu.lamolina.pivot.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.RespuestaEncuestaAlumno;

public interface RespuestaEncuestaAlumnoDAO extends EasyDAO<RespuestaEncuestaAlumno> {

    List<RespuestaEncuestaAlumno> allComentariosByEncuestaDocente(EncuestaDocente encuestaDocente);

}
