package pe.edu.lamolina.pivot.dao.seguridad;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;

public interface UsuarioRolDAO extends EasyDAO<UsuarioRol> {

    List<Long> allInstanciasByUsuarioMenuTipoOficna(Usuario u, Menu menu, TipoOficinaEnum tipoOficina);
    
    List<UsuarioRol> allByUsuarioMenu(Usuario u, Menu menu);

    UsuarioRol findByUsuarioAndRol(Usuario usuario, Rol rol);

    void deleteByUsuarioRol(Usuario usuario, List<Long> roles);

    List<UsuarioRol> allByUser(Usuario user);

    List<UsuarioRol> allByUsuarios(List<Usuario> users);

    UsuarioRol findByUsuarioRol(Usuario usuario, Rol rol);

    UsuarioRol find(UsuarioRol userRol);

    UsuarioRol findUsuarioAndOficina(Usuario usuario1, Oficina oficina);

    List<UsuarioRol> allUsuarioAndOficina(Usuario usuario1, Oficina oficina);

    public void update(Colaborador colaborador, Usuario usuario);

}
