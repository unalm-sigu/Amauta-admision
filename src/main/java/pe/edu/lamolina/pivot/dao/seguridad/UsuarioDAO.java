package pe.edu.lamolina.pivot.dao.seguridad;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface UsuarioDAO extends EasyDAO<Usuario> {

    Usuario findByEmail(String email);

    Usuario findByPersona(Persona persona);

    List<Usuario> allByPersonas(List<Persona> personas);

    List<Usuario> allByFilter(DynatableFilter filter);

    Usuario find(Usuario user);

}
