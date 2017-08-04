package pe.edu.lamolina.pivot.dao.seguridad;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.seguridad.Rol;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.model.seguridad.UsuarioRol;

public interface UsuarioRolDAO extends Crud<UsuarioRol> {

    UsuarioRol findByUsuarioAndRol(Usuario usuario, Rol rol);

    void deleteByUsuarioRol(Usuario usuario, List<Long> roles);

    List<UsuarioRol> allByUser(Usuario user);

}
