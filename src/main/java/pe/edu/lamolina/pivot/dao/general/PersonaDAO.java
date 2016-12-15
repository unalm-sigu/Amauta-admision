package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.general.Persona;

public interface PersonaDAO extends Crud<Persona> {

    List<Persona> allByNombre(String nombre);

}
