package pe.edu.lamolina.pivot.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocenteModalidad;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocenteModalidad;

public interface PuntajeEncuestaDocenteModalidadDAO extends EasyDAO<PuntajeEncuestaDocenteModalidad> {

    List<PuntajeEncuestaDocenteModalidad> allByEncuestaDocenteModalidad(EncuestaDocenteModalidad encuestaDocenteModalidad);

}
