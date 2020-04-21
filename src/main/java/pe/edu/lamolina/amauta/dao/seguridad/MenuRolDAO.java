package pe.edu.lamolina.amauta.dao.seguridad;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.MenuRol;
import pe.edu.lamolina.model.seguridad.Sistema;

public interface MenuRolDAO extends EasyDAO<MenuRol> {

    MenuRol findByMenuRol(MenuRol menurole);

    List<MenuRol> allByMenu(Menu menu);

    List<MenuRol> allBySistema(Sistema sistema);

}
