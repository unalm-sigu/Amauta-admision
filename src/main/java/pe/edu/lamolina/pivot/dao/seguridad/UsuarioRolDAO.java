package pe.edu.lamolina.pivot.dao.seguridad;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;

public interface UsuarioRolDAO extends EasyDAO<UsuarioRol> {

    List<UsuarioRol> allByUsuarioMenu(Usuario u, Menu menu);

    UsuarioRol findByUsuarioAndRol(Usuario usuario, Rol rol);

    void deleteByUsuarioRol(Usuario usuario, List<Long> roles);

    List<UsuarioRol> allByUser(Usuario user);

    List<UsuarioRol> allByUsuarios(List<Usuario> users);

    UsuarioRol findByUsuarioRol(Usuario usuario, Rol rol);

    List<UsuarioRol> allByUsuarioRol(Usuario usuario, Rol rol);

    UsuarioRol find(UsuarioRol userRol);

    UsuarioRol findByUserOficina(Usuario usuario1, Oficina oficina);

    List<UsuarioRol> allByUserOficina(Usuario usuario1, Oficina oficina);

    void updateInactivar(Colaborador colaborador, Usuario usuario);

    List<UsuarioRol> findByUsuario(Usuario usuario);

    List<UsuarioRol> allActivosByUser(Usuario usuario);

    UsuarioRol findByUsuarioAndRolAndEstadoUsuRol(Usuario usuario, Rol rol, UserEstadoEnum estadoUsuarioRol);

    List<UsuarioRol> allWithOfficeByUserRol(Usuario usuario, Rol rol);

    UsuarioRol findByOficinaRolUser(OficinaEnum oficinaEnum, Rol role, Usuario usuario);

}
