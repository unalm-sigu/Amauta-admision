package pe.edu.lamolina.amauta.controller.seguridad.menu;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.MenuRol;
import pe.edu.lamolina.model.seguridad.Rol;

@Component
public class VisorMenu {

    private Map<Long, Menu> mapMenus;
    private Map<Long, Rol> mapRoles;
    private Map<Long, MenuRol> mapMenusRoles;
    private Map<String, MenuRol> mapMenusRolesVisor;

    public Map<Long, Menu> getMapMenus() {
        if (mapMenus == null) {
            mapMenus = new LinkedHashMap();
        }
        return mapMenus;
    }

    public Map<Long, Rol> getMapRoles() {
        if (mapRoles == null) {
            mapRoles = new LinkedHashMap();
        }
        return mapRoles;
    }

    public Map<Long, MenuRol> getMapMenusRoles() {
        if (mapMenusRoles == null) {
            mapMenusRoles = new LinkedHashMap();
        }
        return mapMenusRoles;
    }

    public Map<String, MenuRol> getMapMenusRolesVisor() {
        if (mapMenusRolesVisor == null) {
            mapMenusRolesVisor = new LinkedHashMap();
        }
        return mapMenusRolesVisor;
    }

    public synchronized void setMenus(List<Menu> menus) {
        getMapMenus();
        for (Menu menu : menus) {
            mapMenus.put(menu.getId(), menu);
        }
    }

    public synchronized void setRoles(List<Rol> roles) {
        getMapRoles();
        for (Rol rol : roles) {
            mapRoles.put(rol.getId(), rol);
        }
    }

    public synchronized void setMenusRoles(List<MenuRol> menusRoles) {
        getMapMenus();
        getMapRoles();
        getMapMenusRoles();
        getMapMenusRolesVisor();

        mapMenus.clear();
        mapRoles.clear();
        mapMenusRoles.clear();
        mapMenusRolesVisor.clear();

        for (MenuRol menuRol : menusRoles) {
            addMenuRol(menuRol);
        }
        System.out.println("hay " + mapMenus.size() + " menus");

    }

    public synchronized void addMenuRol(MenuRol menuRol) {
        Rol rol = menuRol.getRol();
        Menu menu = menuRol.getMenu();

        Rol rolBD = mapRoles.get(rol.getId());
        if (rolBD == null) {
            mapRoles.put(rol.getId(), rol);
            rolBD = rol;
            rolBD.setMenuRol(new ArrayList());
        }
        Menu menuBD = mapMenus.get(menu.getId());
        if (menuBD == null) {
            mapMenus.put(menu.getId(), menu);
            menuBD = menu;
            menuBD.setMenuRol(new ArrayList());
            menuBD.setMenus(new ArrayList());
        }

        MenuRol menuRolBD = mapMenusRolesVisor.get(menu.getId() + "-" + rol.getId());
        if (menuRolBD == null) {
            mapMenusRolesVisor.put(menu.getId() + "-" + rol.getId(), menuRol);
            mapMenusRoles.put(menuRol.getId(), menuRol);
            menuRolBD = menuRol;
        }

        menuRolBD.setRol(rolBD);
        menuRolBD.setMenu(menuBD);
        rolBD.getMenuRol().add(menuRolBD);
        menuBD.getMenuRol().add(menuRolBD);
    }

    public synchronized void deleteMenuRol(MenuRol menuRol) {
        Rol rol = menuRol.getRol();
        Menu menu = menuRol.getMenu();
        MenuRol menuRolBD = mapMenusRolesVisor.get(menu.getId() + "-" + rol.getId());
        if (menuRolBD == null) {
            return;
        }

        Rol rolBD = menuRolBD.getRol();
        Menu menuBD = menuRolBD.getMenu();
        rolBD.getMenuRol().remove(menuRolBD);
        menuBD.getMenuRol().remove(menuRolBD);
        mapMenusRolesVisor.remove(menu.getId() + "-" + rol.getId());

    }

    public synchronized MenuRol getMenuRol(MenuRol menuRol) {
        Rol rol = menuRol.getRol();
        Menu menu = menuRol.getMenu();
        return mapMenusRolesVisor.get(menu.getId() + "-" + rol.getId());
    }

}
