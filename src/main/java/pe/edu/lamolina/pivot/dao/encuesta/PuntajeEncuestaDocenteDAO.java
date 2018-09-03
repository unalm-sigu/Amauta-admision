package pe.edu.lamolina.pivot.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocente;

public interface PuntajeEncuestaDocenteDAO extends EasyDAO<PuntajeEncuestaDocente> {

    List<PuntajeEncuestaDocente> allByEncuestaDocente(EncuestaDocente encuestaDocente);

}
