package pe.edu.lamolina.pivot.dao.seguridad;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.seguridad.Rol;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;

public interface RolDAO extends Crud<Rol> {

    List<Rol> allActivoByUsuario(Usuario usuario);

}
