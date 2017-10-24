package pe.edu.lamolina.pivot.dao.seguridad;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.seguridad.Menu;
import pe.edu.lamolina.pivot.model.seguridad.Rol;
import pe.edu.lamolina.pivot.model.seguridad.Sistema;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;

public interface RolDAO extends Crud<Rol> {
    
    List<Rol> allByUser(Usuario usuario, Sistema sistema);

    List<Rol> allRolMenu(Menu menu);

    List<Rol> allRol(List<Rol> rolesMenu);

    List<Rol> allActivoByUsuario(Usuario usuario);

}
