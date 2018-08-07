package pe.edu.lamolina.pivot.controller.seguridad.rol;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jboss.logging.annotations.Transform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.model.enums.FuncionRolEstadoEnum;
import pe.edu.lamolina.model.enums.MenuTipoEnum;
import pe.edu.lamolina.model.enums.TipoPerfilCompaniaEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.seguridad.FuncionRol;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.pivot.dao.general.PerfilCompaniaDAO;
import pe.edu.lamolina.pivot.dao.seguridad.FuncionRolDAO;
import pe.edu.lamolina.pivot.dao.seguridad.MenuDAO;
import pe.edu.lamolina.pivot.dao.seguridad.MenuRolDAO;
import pe.edu.lamolina.pivot.dao.seguridad.RolDAO;

@Service
@Transactional(readOnly = true)
public class RolServiceImp implements RolService {

    @Autowired
    MenuDAO menuDAO;

    @Autowired
    RolDAO rolDAO;

    @Autowired
    MenuRolDAO menuRolDAO;

    @Autowired
    FuncionRolDAO funcionRolDAO;

    @Autowired
    PerfilCompaniaDAO perfilCompaniaDAO;

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

    @Override
    public List<Rol> allRolByDynatable(DynatableFilter filter) {
        return rolDAO.allByDynatable(filter);
    }

    @Override
    @Transactional
    public void saveFuncionRol(FuncionRol funcionRol) {

        FuncionRol funcionRolDb = funcionRolDAO.findByRolPerfilCompania(funcionRol);
        if (funcionRolDb != null) {
            funcionRolDb.setFechaActivacion(new Date());
            funcionRolDb.setEstado(FuncionRolEstadoEnum.ACT.name());
            funcionRolDAO.update(funcionRolDb);
            return;
        }
        funcionRol.setFechaActivacion(new Date());
        funcionRol.setEstado(FuncionRolEstadoEnum.ACT.name());
        funcionRolDAO.save(funcionRol);
    }

    @Override
    @Transactional
    public void cambiarEstado(FuncionRol funcionRol) {
        FuncionRol funcionRolDb = funcionRolDAO.find(funcionRol);
        if (FuncionRolEstadoEnum.ACT.name().equalsIgnoreCase(funcionRol.getEstado())) {
            funcionRolDb.setEstado(FuncionRolEstadoEnum.ACT.name());
            funcionRolDb.setFechaActivacion(new Date());
            funcionRolDAO.update(funcionRolDb);
        } else if (FuncionRolEstadoEnum.INA.name().equalsIgnoreCase(funcionRol.getEstado())) {
            funcionRolDb.setFechaDesactivacion(new Date());
            funcionRolDb.setEstado(FuncionRolEstadoEnum.INA.name());
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
        return funcionRolDAO.allFuncionRolByRoles(roles);
    }

    @Override
    public ArrayNode allPerfilCompania(Rol rol, Map<Long, List<FuncionRol>> funcionesRolMap, TipoPerfilCompaniaEnum tipoPerfilCompaniaEnum) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        ArrayNode array = new ArrayNode(jsonFactory);

        List<FuncionRol> funcionesRoll = funcionesRolMap.get(rol.getId());
        if (funcionesRoll == null || funcionesRoll.isEmpty()) {
            return array;
        }

        for (FuncionRol funcionRol : funcionesRoll) {

            if (FuncionRolEstadoEnum.ACT.name().equalsIgnoreCase(funcionRol.getEstado())) {
                if (tipoPerfilCompaniaEnum.name().equalsIgnoreCase(funcionRol.getPerfilCompania().getTipo())) {

                    ObjectNode node = JsonHelper.createJson(funcionRol.getPerfilCompania(), jsonFactory, true,
                            new String[]{
                                "*"
                            });

                    array.add(node);
                }
            }
        }

        return array;
    }

}
