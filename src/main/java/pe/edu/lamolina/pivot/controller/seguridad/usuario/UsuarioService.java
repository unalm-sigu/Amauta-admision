package pe.edu.lamolina.pivot.controller.seguridad.usuario;

import java.util.List;
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.model.general.TipoDocIdentidad;
import pe.edu.lamolina.pivot.model.seguridad.Rol;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.model.seguridad.UsuarioRol;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface UsuarioService {

    List<Usuario> allByDynatable(DynatableFilter filter);

    Usuario findUsuario(Usuario usuario);

    void desactivaUsuario(Usuario usuario, DataSessionPivot ds);

    void activaUsuario(Usuario usuario, DataSessionPivot ds);

    List<TipoDocIdentidad> allDocumentos();

    List<UsuarioRol> allRolesByUser(Usuario user);

    void deshabilitarPerfil(UsuarioRol userRol, DataSessionPivot ds);

    void saveUsuario(Usuario usuario, DataSessionPivot ds);

    String validarEmailByPersona(String email, Persona persona);

    String validarEmailCompaniaByPersona(String email, Persona persona);

    Persona findPersona(Persona personaTmp);

    List<Rol> allRolesWithoutUser(Usuario user);

    void saveUserRol(UsuarioRol userRol, DataSessionPivot ds);

    List<Rol> listRol();

}
