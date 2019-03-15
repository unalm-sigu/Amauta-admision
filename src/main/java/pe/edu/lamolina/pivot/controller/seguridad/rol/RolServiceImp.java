package pe.edu.lamolina.pivot.controller.seguridad.rol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.enums.FuncionRolEstadoEnum;
import pe.edu.lamolina.model.enums.MenuTipoEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.seguridad.FuncionRol;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.RolSistema;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.general.PerfilCompaniaDAO;
import pe.edu.lamolina.pivot.dao.seguridad.FuncionRolDAO;
import pe.edu.lamolina.pivot.dao.seguridad.MenuDAO;
import pe.edu.lamolina.pivot.dao.seguridad.MenuRolDAO;
import pe.edu.lamolina.pivot.dao.seguridad.RolDAO;
import pe.edu.lamolina.pivot.dao.seguridad.RolSistemaDAO;

@Service
@Transactional(readOnly = true)
public class RolServiceImp implements RolService {

    @Autowired
    MenuDAO menuDAO;

    @Autowired
    RolDAO rolDAO;

    @Autowired
    RolSistemaDAO rolSistemaDAO;

    @Autowired
    MenuRolDAO menuRolDAO;

    @Autowired
    FuncionRolDAO funcionRolDAO;

    @Autowired
    PerfilCompaniaDAO perfilCompaniaDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Transactional
    public void save(Rol rol, Sistema sistema) {

        ObjectUtil.eliminarAttrSinId(rol, "rolSuperior");

        Rol rolCode = rolDAO.findByCode(rol.getCodigo());
        if (rolCode != null) {
            throw new PhobosException("Código ya registrado");
        }
        Boolean existEnum = false;
        for (RolEnum enu : RolEnum.values()) {
            existEnum = rol.getCodigo().equals(enu.name()) ? true : false;
        }
        Assert.isTrue(existEnum, "No se agregó el código. Comunicarse con soporte.");
        rolDAO.save(rol);

        RolSistema rolSistema = new RolSistema();
        rolSistema.setRol(rol);
        rolSistema.setSistema(sistema);
        rolSistemaDAO.save(rolSistema);
    }

    @Override
    @Transactional
    public void update(Rol rol) {

        ObjectUtil.eliminarAttrSinId(rol, "rolSuperior");

        Rol rolCode = rolDAO.findByCode(rol.getCodigo());

        if (rolCode != null) {
            if (rolCode.getId().longValue() != rol.getId()) {
                throw new PhobosException("Código ya registrado");
            }
        }

        Rol rolDb = rolDAO.find(rol.getId());

        if (rol.getRolSuperior() != null) {
            if (rol.getRolSuperior().getId() == rolDb.getId().longValue()) {
                throw new PhobosException("No puede asignar como rol superior al mismo rol");
            }
        }

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
    public void delete(Rol rol, Sistema sistema) {

        RolSistema rolSistema = rolSistemaDAO.findByRolSistema(rol, sistema);
        rolSistemaDAO.delete(rolSistema);

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

    @Override
    public List<Rol> allRolByDynatable(DynatableFilter filter, Sistema sistema) {
        List<Rol> roles = rolDAO.allByDynatable(filter, sistema);
        List<RolSistema> rolesSys = rolSistemaDAO.allByRoles(roles);
        Map<Long, List<RolSistema>> mapRolSys = TypesUtil.convertListToMapList("rol.id", rolesSys);
        for (Rol rol : roles) {
            List<RolSistema> systemsRol = mapRolSys.get(rol.getId());
            systemsRol = (systemsRol == null) ? new ArrayList() : systemsRol;
            rol.setRolSistema(systemsRol);
        }

        return roles;
    }

    @Override
    @Transactional
    public void saveFuncionRol(FuncionRol funcionRol, Usuario usuario) {

        FuncionRol funcionRolDb = funcionRolDAO.findByRolPerfilCompania(funcionRol);
        if (funcionRolDb != null) {
            funcionRolDb.setFechaActivacion(new Date());
            funcionRolDb.setEstado(FuncionRolEstadoEnum.ACT.name());
            funcionRolDAO.update(funcionRolDb);
            return;
        }
        funcionRol.setFechaActivacion(new Date());
        funcionRol.setEstado(FuncionRolEstadoEnum.ACT.name());
        funcionRol.setUsuarioActivacion(usuario);
        funcionRolDAO.save(funcionRol);
    }

    @Override
    @Transactional
    public void cambiarEstado(FuncionRol funcionRol, Usuario usuario) {
        FuncionRol funcionRolDb = funcionRolDAO.find(funcionRol);
        if (FuncionRolEstadoEnum.ACT.name().equalsIgnoreCase(funcionRol.getEstado())) {
            funcionRolDb.setEstado(FuncionRolEstadoEnum.ACT.name());
            funcionRolDb.setUsuarioActivacion(usuario);
            funcionRolDb.setFechaActivacion(new Date());
            funcionRolDAO.update(funcionRolDb);
        } else if (FuncionRolEstadoEnum.INA.name().equalsIgnoreCase(funcionRol.getEstado())) {
            funcionRolDb.setFechaDesactivacion(new Date());
            funcionRolDb.setEstado(FuncionRolEstadoEnum.INA.name());
            funcionRolDb.setUsuarioDesactivacion(usuario);
            funcionRolDAO.update(funcionRolDb);
        }
    }

    @Override
    public List<PerfilCompania> allPerfilCompaniaByTipo(PerfilCompania perfilCompania, Compania compania) {
        return perfilCompaniaDAO.allPerfilCompaniaByTipo(perfilCompania, compania);
    }

    @Override
    public List<FuncionRol> allFuncionRolTipoPerfil(FuncionRol funcionRol) {
        return funcionRolDAO.allFuncionRolTipoPerfil(funcionRol);
    }

    @Override
    public List<FuncionRol> allFuncionRol(List<Rol> roles) {
        if (roles == null || roles.isEmpty()) {
            return new ArrayList<>();
        }
        return funcionRolDAO.allFuncionRolActivoByRoles(roles);
    }

    @Override
    public List<Rol> allRolSuperior(String nombre) {
        return rolDAO.allRolSuperior(nombre);
    }

}
