package pe.edu.lamolina.pivot.controller.seguridad.menu;

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
import pe.edu.lamolina.pivot.dao.seguridad.RolSistemaDAO;
import pe.edu.lamolina.pivot.dao.seguridad.SistemaDAO;
import pe.edu.lamolina.pivot.model.seguridad.Menu;
import pe.edu.lamolina.pivot.model.seguridad.MenuRol;
import pe.edu.lamolina.pivot.model.seguridad.Rol;
import pe.edu.lamolina.pivot.model.seguridad.RolSistema;
import pe.edu.lamolina.pivot.model.seguridad.Sistema;
import pe.edu.lamolina.pivot.zelper.enums.MenuTipoEnum;
import pe.albatross.zelpers.miscelanea.Assert;

@Service
@Transactional(readOnly = true)
public class MenuServiceImp implements MenuService {

    @Autowired
    MenuDAO menuDAO;

    @Autowired
    RolDAO rolDAO;

    @Autowired
    MenuRolDAO menuRolDAO;

    @Autowired
    RolSistemaDAO rolSistemaDAO;

    @Autowired
    SistemaDAO sistemaDAO;

    @Autowired
    VisorMenu visorMenu;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<Menu> allMenuSystem(Sistema sistema) {
        List<Menu> menus = menuDAO.allMenuSystem(sistema);
        return allMenuOrdered(menus);
    }

    @Override
    @Transactional
    public void save(Menu menu) {

        menu.setSistema(new Sistema(1L));
        Integer mayororden = null;
        if (MenuTipoEnum.TITULO.name().equals(menu.getTipo())) {
            mayororden = menuDAO.getMayorOrdenTipo(new Sistema(1L), MenuTipoEnum.TITULO);
        } else {
            mayororden = menuDAO.getMayorOrdenGrupo(new Sistema(1L), menu.getMenuSuperior());
        }
        if (mayororden != null) {
            mayororden++;
        } else {
            mayororden = 1;
        }
        menu.setOrden(mayororden);
        menuDAO.save(menu);
    }

    @Override
    @Transactional
    public void update(Menu menu) {
        Menu menudB = menuDAO.find(menu.getId());
        menudB.setIcono(menu.getIcono());
        menudB.setRuta(menu.getRuta());
        menudB.setNombre(menu.getNombre());
        menudB.setTipo(menu.getTipoEnum());
        menuDAO.update(menudB);
    }

    @Override
    public Menu find(Menu menu) {
        return menuDAO.find(menu.getId());
    }

    @Override
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
            //logger.debug("{}{} :::: {}", tab, menu.getOrden(), menu.getNombre());
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
    public void delete(Menu menu) {
        List<MenuRol> menuRoles = menuRolDAO.allByMenu(menu);
        Assert.isTrue(menuRoles.isEmpty(), "Este menú no puede ser eliminado porque tiene elementos relacionados a este");
        menuDAO.delete(menu);
    }

    @Override
    public List<Rol> allRol(List<Rol> rolesMenu) {
        if (rolesMenu.size() < 1) {
            return rolDAO.all();
        }
        return rolDAO.allRol(rolesMenu);
    }

    @Override
    public List<Rol> allRolMenu(Menu menu) {
        return rolDAO.allRolMenu(menu);
    }

    @Override
    public Menu getMenu(Menu menu) {
        return menuDAO.find(menu.getId());
    }

    @Override
    @Transactional
    public void desAsignarRol(Menu menu) {
        List<MenuRol> menuRoles = menu.getMenuRol();
        for (MenuRol menuRol : menuRoles) {
            MenuRol menuRolBD = menuRolDAO.findByMenuRol(menuRol);
            if (menuRolBD == null) {
                return;
            }

            Menu menuBD = menuRolBD.getMenu();
            Rol rolBD = menuRolBD.getRol();
            Sistema sysBD = menuBD.getSistema();

            visorMenu.deleteMenuRol(menuRol);
            menuRolDAO.delete(menuRolBD);

            Sistema sys = sistemaDAO.findByRolSistema(menuRolBD.getRol(), sysBD);
            RolSistema rolSys = rolSistemaDAO.findByRolSistema(rolBD, sysBD);
            if (sys == null && rolSys != null) {
                rolSistemaDAO.delete(rolSys);
            }
        }
    }

    @Override
    @Transactional
    public void asignarRol(Menu menu) {
        List<MenuRol> menuRoles = menu.getMenuRol();
        for (MenuRol menuRol : menuRoles) {
            logger.debug("{}",menuRol.getMenu().getId());
            logger.debug("{}",menuRol.getRol().getId());
            MenuRol menuRolBD = visorMenu.getMenuRol(menuRol);
            if (menuRolBD == null) {
                menuRolBD = menuRolDAO.findByMenuRol(menuRol);
                if (menuRolBD == null) {
                    menuRolBD = new MenuRol();
                    Menu menuBD = menuDAO.find(menuRol.getMenu().getId());
                    menuRolBD.setMenu(menuBD);
                    menuRolBD.setRol(menuRol.getRol());
                    menuRolDAO.save(menuRolBD);
                }
                visorMenu.addMenuRol(menuRolBD);
            }

            RolSistema rolSys = rolSistemaDAO.findByRolSistema(menuRolBD.getRol(), menuRolBD.getMenu().getSistema());
            if (rolSys == null) {
                rolSys = new RolSistema();
                rolSys.setRol(menuRolBD.getRol());
                rolSys.setSistema(menuRolBD.getMenu().getSistema());
                rolSistemaDAO.save(rolSys);
            }
        }
    }

    @Override
    public List<MenuTipoEnum> allTiposMenuBySuperior(Menu menu) {
        List<MenuTipoEnum> tipos = new ArrayList();

        if (menu == null) {
            tipos.add(MenuTipoEnum.TITULO);
            return tipos;
        }

        if (null != menu.getTipoEnum()) {
            switch (menu.getTipoEnum()) {
                case TITULO:
                    tipos.add(MenuTipoEnum.MENU);
                    tipos.add(MenuTipoEnum.MENU_PADRE);
                    break;
                case MENU:
                    tipos.add(MenuTipoEnum.OPCION);
                    tipos.add(MenuTipoEnum.BOTON);
                    break;
                case MENU_PADRE:
                    tipos.add(MenuTipoEnum.SUB_MENU);
                    break;
                case SUB_MENU:
                    tipos.add(MenuTipoEnum.OPCION);
                    tipos.add(MenuTipoEnum.BOTON);
                    break;
                default:
                    break;
            }
        }

        return tipos;
    }

    @Override
    @Transactional
    public void itemMenuUp(Menu menu) {
        //logger.debug("ITEM UP");
        Menu menuDb = menuDAO.find(menu.getId());
        if (menuDb.getOrden() == null) {
            menuDb.setOrden(1);
            menuDAO.update(menuDb);
            return;
        }
        List<Menu> menus = new ArrayList<>();
        if (menuDb.getTipo().equals(MenuTipoEnum.TITULO.name())) {
            menus = menuDAO.allByTipo(MenuTipoEnum.TITULO, new Sistema(1L));
        } else {
            menus = menuDAO.allBySuperMenu(new Sistema(1L), menuDb.getMenuSuperior());
        }
        Integer tamano = menus.size();
        //logger.debug("TAMANO CATEGORY {}", tamano);
        if (tamano < 1) {
            return;
        }
        if (tamano.intValue() < menuDb.getOrden()) {
            //logger.debug("COMPARANDO  {} DE <=  {}", tamano.intValue(), menuDb.getOrden());
            return;
        }

        Menu menuOrdenRequerido = null;
        Integer orden = menuDb.getOrden();
        //logger.debug("ORDEN ORIGINAL {}", orden);
        Integer ordenup = orden - 1;
        //logger.debug("ORDEN A SUBIR  {}", ordenup);

        if (menuDb.getTipo().equals(MenuTipoEnum.TITULO.name())) {
            menuOrdenRequerido = menuDAO.findByTipoOrden(MenuTipoEnum.TITULO, new Sistema(1L), ordenup);
        } else {
            menuOrdenRequerido = menuDAO.findBySuperMenuOrden(new Sistema(1L), menuDb.getMenuSuperior(), ordenup);
        }

        if (menuOrdenRequerido != null) {
            //logger.debug("MENU ORDEN OCUPADO {}", menuOrdenRequerido.getId());
            menuOrdenRequerido.setOrden(orden);
            //logger.debug("MENU ORDEN OCUPADO ASIGNADO {}", menuOrdenRequerido.getOrden());
            menuDAO.update(menuOrdenRequerido);
        }

        if (ordenup > 0) {
            menuDb.setOrden(ordenup);
            //logger.debug("MENU ORDEN ASIGNADO {}", menuDb.getOrden());
            menuDAO.update(menuDb);
        }

    }

    @Override
    @Transactional
    public void itemMenuDown(Menu menu) {
        //logger.debug("ITEM DOWN");

        Menu menuDb = menuDAO.find(menu.getId());
        if (menuDb.getOrden() == null) {
            menuDb.setOrden(1);
            menuDAO.update(menuDb);
            return;
        }
        List<Menu> menus = new ArrayList<>();
        if (menuDb.getTipo().equals(MenuTipoEnum.TITULO.name())) {
            menus = menuDAO.allByTipo(MenuTipoEnum.TITULO, new Sistema(1L));
        } else {
            menus = menuDAO.allBySuperMenu(new Sistema(1L), menuDb.getMenuSuperior());
        }
        Integer tamano = menus.size();
        if (tamano < 1) {
            return;
        }
        if (menuDb.getOrden() < 1) {
            return;
        }

        Menu menuOrdenRequerido = null;
        Integer orden = menuDb.getOrden();
        Integer ordendown = orden + 1;

        if (menuDb.getTipo().equals(MenuTipoEnum.TITULO.name())) {
            menuOrdenRequerido = menuDAO.findByTipoOrden(MenuTipoEnum.TITULO, new Sistema(1L), ordendown);
        } else {
            menuOrdenRequerido = menuDAO.findBySuperMenuOrden(new Sistema(1L), menuDb.getMenuSuperior(), ordendown);
        }

        if (menuOrdenRequerido != null) {
            menuOrdenRequerido.setOrden(orden);
            menuDAO.update(menuOrdenRequerido);
        }
        
        if (ordendown <= tamano) {
            menuDb.setOrden(ordendown);
            menuDAO.update(menuDb);
        }
    }

    @Override
    public void inicializarMenus() {
        List<MenuRol> menusRoles = menuRolDAO.allBySistema(new Sistema(1L));
        visorMenu.setMenusRoles(menusRoles);
    }

}
