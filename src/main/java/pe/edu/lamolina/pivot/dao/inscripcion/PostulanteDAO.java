package pe.edu.lamolina.pivot.dao.inscripcion;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface PostulanteDAO extends EasyDAO<Postulante> {

    List<Postulante> allByPersona(Persona persona);

}
