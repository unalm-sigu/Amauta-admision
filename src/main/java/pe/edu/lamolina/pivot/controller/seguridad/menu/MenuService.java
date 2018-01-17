package pe.edu.lamolina.pivot.controller.seguridad.menu;

import java.util.List;
import pe.edu.lamolina.model.enums.MenuTipoEnum;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Sistema;

public interface MenuService {

    List<Menu> allMenuSystem(Sistema sistema);

    void save(Menu menu);

    void update(Menu menu);

    Menu find(Menu menu);

    List<Menu> allMenuOrdered(List<Menu> menusBD);

    void delete(Menu menu);

    List<Rol> allRolMenu(Menu menu);

    List<Rol> allRol(List<Rol> rolesMenu);

    Menu getMenu(Menu menu);

    void desAsignarRol(Menu menu);

    void asignarRol(Menu menu);

    List<MenuTipoEnum> allTiposMenuBySuperior(Menu menu);

    void itemMenuUp(Menu menu);

    void itemMenuDown(Menu menu);

    void inicializarMenus();

}
