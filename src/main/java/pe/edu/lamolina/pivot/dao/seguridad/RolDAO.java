package pe.edu.lamolina.pivot.dao.seguridad;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface RolDAO extends EasyDAO<Rol> {

    List<Rol> allByUser(Usuario usuario, Sistema sistema);

    List<Rol> allRolMenu(Menu menu);

    List<Rol> allRol(List<Rol> rolesMenu);

    List<Rol> allActivoByUsuario(Usuario usuario);

    Rol findByCode(RolEnum rolEnum);

    List<Rol> allByDynatable(DynatableFilter filter);

}
