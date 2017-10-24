package pe.edu.lamolina.pivot.controller.seguridad.rol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.pivot.dao.seguridad.MenuDAO;
import pe.edu.lamolina.pivot.dao.seguridad.MenuRolDAO;
import pe.edu.lamolina.pivot.dao.seguridad.RolDAO;
import pe.edu.lamolina.pivot.model.seguridad.Menu;
import pe.edu.lamolina.pivot.model.seguridad.Rol;
import pe.edu.lamolina.pivot.model.seguridad.Sistema;
import pe.edu.lamolina.pivot.zelper.enums.MenuTipoEnum;

@Service
@Transactional(readOnly = true)
public class RolServiceImp implements RolService {

    @Autowired
    MenuDAO menuDAO;

    @Autowired
    RolDAO rolDAO;

    @Autowired
    MenuRolDAO menuRolDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Transactional
    public void save(Rol rol) {
        rolDAO.save(rol);
    }

    @Override
    @Transactional
    public void update(Rol rol) {
        rolDAO.update(rol);
    }

    public List<Menu> allMenuOrdered(List<Menu> menusBD) {
        List<Menu> menusMain = new ArrayList();
        Map<Long, Menu> mapMenusOrder = new LinkedHashMap();

        for (Menu menuBD : menusBD) {
            Menu menu = (Menu) ObjectUtil.getParentTree(menuBD, "menuSuperior.menuSuperior.menuSuperior");
            if (crearMenu(menu, mapMenusOrder, MenuTipoEnum.TITULO, menusMain) != null) {
                continue;
            }

            menu = (Menu) ObjectUtil.getParentTree(menuBD, "menuSuperior.menuSuperior");
            if (crearMenu(menu, mapMenusOrder, MenuTipoEnum.TITULO, menusMain) != null) {
                continue;
            }

            menu = (Menu) ObjectUtil.getParentTree(menuBD, "menuSuperior");
            if (crearMenu(menu, mapMenusOrder, MenuTipoEnum.TITULO, menusMain) != null) {
                continue;
            }

            crearMenu(menuBD, mapMenusOrder, MenuTipoEnum.TITULO, menusMain);
        }

        for (Menu menuBD : menusBD) {
            Menu menu = (Menu) ObjectUtil.getParentTree(menuBD, "menuSuperior.menuSuperior");
            if (crearMenu(menu, mapMenusOrder, MenuTipoEnum.MENU) != null) {
                continue;
            }

            menu = (Menu) ObjectUtil.getParentTree(menuBD, "menuSuperior.menuSuperior");
            if (crearMenu(menu, mapMenusOrder, MenuTipoEnum.MENU_PADRE) != null) {
                continue;
            }

            menu = (Menu) ObjectUtil.getParentTree(menuBD, "menuSuperior");
            if (crearMenu(menu, mapMenusOrder, MenuTipoEnum.MENU) != null) {
                continue;
            }

            menu = (Menu) ObjectUtil.getParentTree(menuBD, "menuSuperior");
            if (crearMenu(menu, mapMenusOrder, MenuTipoEnum.MENU_PADRE) != null) {
                continue;
            }

            if (crearMenu(menuBD, mapMenusOrder, MenuTipoEnum.MENU) != null) {
                continue;
            }

            crearMenu(menuBD, mapMenusOrder, MenuTipoEnum.MENU_PADRE);
        }

        for (Menu menuBD : menusBD) {
            Menu menu = (Menu) ObjectUtil.getParentTree(menuBD, "menuSuperior");
            if (crearMenu(menu, mapMenusOrder, MenuTipoEnum.SUB_MENU) != null) {
                continue;
            }
            crearMenu(menuBD, mapMenusOrder, MenuTipoEnum.SUB_MENU);
        }

        for (Menu menuBD : menusBD) {
            if (crearMenu(menuBD, mapMenusOrder, MenuTipoEnum.OPCION) != null) {
                continue;
            }
            crearMenu(menuBD, mapMenusOrder, MenuTipoEnum.BOTON);
        }

        ordenarItems(menusMain, "");
        return menusMain;
    }

    private void ordenarItems(List<Menu> menus, String tab) {
        Collections.sort(menus, new Menu.CompareOrden());
        menus.stream().map(menu -> {
            logger.debug("{}{} :::: {}", tab, menu.getOrden(), menu.getNombre());
            return menu;
        }).forEachOrdered(menu -> {
            ordenarItems(menu.getMenus(), tab + "    ");
        });
    }

    private Menu crearMenu(Menu menu, Map<Long, Menu> mapMenusOrder, MenuTipoEnum tipoMenu) {
        return crearMenu(menu, mapMenusOrder, tipoMenu, null);
    }

    private Menu crearMenu(Menu menu, Map<Long, Menu> mapMenusOrder, MenuTipoEnum tipoMenu, List<Menu> menusMain) {
        if (menu == null) {
            return menu;
        }

        if (menu.getTipoEnum() == tipoMenu) {
            Menu menuMap = mapMenusOrder.get(menu.getId());
            if (menuMap == null) {
                menu.setMenus(new ArrayList());
                mapMenusOrder.put(menu.getId(), menu);

                if (menusMain != null) {
                    menusMain.add(menu);
                }

                if (tipoMenu != MenuTipoEnum.TITULO) {
                    Menu parent = mapMenusOrder.get(menu.getMenuSuperior().getId());
                    parent.getMenus().add(menu);
                }
                return menu;
            }
        }
        return null;
    }

    @Override
    @Transactional
    public void delete(Rol rol) {
        rolDAO.delete(rol);
    }

    @Override
    public List<Rol> allRol() {
        return rolDAO.all();
    }

    @Override
    public List<Menu> allMenuSystemByRol(Sistema sistema, Long idRol) {
        List<Menu> menus = menuDAO.allMenuSystemByRol(sistema, idRol);
        return allMenuOrdered(menus);
    }

    @Override
    public Rol findRol(Rol rol) {
        return rolDAO.find(rol.getId());
    }

}
