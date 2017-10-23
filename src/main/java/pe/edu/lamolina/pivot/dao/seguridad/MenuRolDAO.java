package pe.edu.lamolina.pivot.dao.seguridad;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.seguridad.Menu;
import pe.edu.lamolina.pivot.model.seguridad.MenuRol;
import pe.edu.lamolina.pivot.model.seguridad.Sistema;

public interface MenuRolDAO extends Crud<MenuRol> {

    MenuRol findByMenuRol(MenuRol menurole);

    List<MenuRol> allByMenu(Menu menu);

    List<MenuRol> allBySistema(Sistema sistema);

}
