package pe.edu.lamolina.pivot.dao.inscripcion;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.model.inscripcion.Postulante;

public interface PostulanteDAO extends Crud<Postulante> {

    List<Postulante> allByPersona(Persona persona);

}
