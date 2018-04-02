package pe.edu.lamolina.pivot.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.encuesta.EncuestaAlumno;
import pe.edu.lamolina.model.encuesta.EncuestaDocente;

public interface EncuestaAlumnoDAO extends EasyDAO<EncuestaAlumno> {

    public List<EncuestaAlumno> allByEncuestaDocente(EncuestaDocente encuesta);

}
