package pe.edu.lamolina.pivot.dao.seguridad;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;

public interface UsuarioDAO extends Crud<Usuario> {

    Usuario findByEmail(String email);

    Usuario findByPersona(Persona persona);

    List<Usuario> allByPersonas(List<Persona> personas);

}
