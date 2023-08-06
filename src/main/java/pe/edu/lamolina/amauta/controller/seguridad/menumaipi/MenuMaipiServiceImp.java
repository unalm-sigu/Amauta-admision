package pe.edu.lamolina.amauta.controller.seguridad.menumaipi;

import pe.edu.lamolina.amauta.controller.seguridad.menu.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.amauta.dao.seguridad.MenuDAO;
import pe.edu.lamolina.amauta.dao.seguridad.MenuRolDAO;
import pe.edu.lamolina.amauta.dao.seguridad.RolDAO;
import pe.edu.lamolina.amauta.dao.seguridad.RolSistemaDAO;
import pe.edu.lamolina.amauta.dao.seguridad.SistemaDAO;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.model.enums.MenuTipoEnum;
import static pe.edu.lamolina.model.enums.MenuTipoEnum.MENU;
import static pe.edu.lamolina.model.enums.MenuTipoEnum.MENU_PADRE;
import static pe.edu.lamolina.model.enums.MenuTipoEnum.TITULO;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.MenuRol;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.RolSistema;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.amauta.config.DespliegueConfig;
import pe.edu.lamolina.model.constantines.BienestarConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class MenuMaipiServiceImp implements MenuMaipiService {

    private final MenuDAO menuDAO;
    private final MenuRolDAO menuRolDAO;
    private final RolDAO rolDAO;
    private final RolSistemaDAO rolSistemaDAO;
    private final SistemaDAO sistemaDAO;
    private final VisorMenu visorMenu;

    private final DespliegueConfig despliegueConfig;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public List<Menu> allMenuSystem(Sistema sistema) {
        List<Menu> menus = menuDAO.allMenuSystem(sistema);
        for (Menu menu : menus) {
            menu.setEstadoEnum(this.getEstadoMenu(menu));
        }
        return allMenuOrdered(menus);
    }

    @Override
    @Transactional
    public void save(Menu menu) {
        menu.setSistema(new Sistema(GlobalConstantine.ID_SISTEMA_MAIPI));
        Integer mayorOrden = null;

        if (MenuTipoEnum.TITULO.name().equals(menu.getTipo())) {
            mayorOrden = menuDAO.getMayorOrdenTipo(new Sistema(GlobalConstantine.ID_SISTEMA_MAIPI), MenuTipoEnum.TITULO);
        } else {
            mayorOrden = menuDAO.getMayorOrdenGrupo(new Sistema(GlobalConstantine.ID_SISTEMA_MAIPI), menu.getMenuSuperior());
        }

        if (mayorOrden != null) {
            mayorOrden++;
        } else {
            mayorOrden = 1;
        }

        menu.setOrden(mayorOrden);
        menu.setClave(RandomStringUtils.randomAlphanumeric(20));
        menu.setEntornos(GlobalConstantine.AMBIENTES);
        menu.setModalidades(GlobalConstantine.MODALIDADES);

        this.setEstadoMenu(menu, menu);
        this.setModalidades(menu, menu);

        menuDAO.save(menu);
    }

    @Override
    @Transactional
    public void update(Menu menu) {
        Menu menuBD = menuDAO.find(menu.getId());
        menuBD.setIcono(menu.getIcono());
        menuBD.setRuta(menu.getRuta());
        menuBD.setNombre(menu.getNombre());
        menuBD.setBucleActivar(menu.getBucleActivar());
        menuBD.setTipoEnum(menu.getTipoEnum());

        this.setEstadoMenu(menu, menuBD);
        this.setModalidades(menu, menuBD);

        menuDAO.update(menuBD);
    }

    @Override
    public void reloadMenusMaipi() {
        rabbitTemplate.convertAndSend(BienestarConstantine.QUEUE_PROCESOS_MAIPI, "LOAD_MENU");
    }

    @Override
    public Menu find(Menu menu) {
        Menu menuBD = menuDAO.find(menu.getId());
        List<Menu> menusHijos = menuDAO.allBySuperMenu(menuBD.getSistema(), menuBD);
        menuBD.setMenus(menusHijos);
        menuBD.setEstadoEnum(getEstadoMenu(menuBD));

        List<String> modalidades = Arrays.asList(menuBD.getModalidades().split(","));
        menuBD.setPregrado(getModalidadMenu(modalidades, ModalidadEstudioEnum.PRE));
        menuBD.setPosgrado(getModalidadMenu(modalidades, ModalidadEstudioEnum.EPG));
        menuBD.setEspecial(getModalidadMenu(modalidades, ModalidadEstudioEnum.ESP));
        menuBD.setVisitante(getModalidadMenu(modalidades, ModalidadEstudioEnum.VIS));

        return menuBD;
    }

    private String getModalidadMenu(List<String> modalidades, ModalidadEstudioEnum modalidadEnum) {
        if (modalidades.contains(modalidadEnum.name())) {
            return EstadoEnum.ACT.name();
        }
        return EstadoEnum.INA.name();
    }

    private void setModalidades(Menu menuForm, Menu menuBD) {
        List<String> listaModalidades = new ArrayList(Arrays.asList(GlobalConstantine.MODALIDADES.split(",")));
        this.verificar(listaModalidades, menuForm.getPregrado(), ModalidadEstudioEnum.PRE);
        this.verificar(listaModalidades, menuForm.getPosgrado(), ModalidadEstudioEnum.EPG);
        this.verificar(listaModalidades, menuForm.getEspecial(), ModalidadEstudioEnum.ESP);
        this.verificar(listaModalidades, menuForm.getVisitante(), ModalidadEstudioEnum.VIS);

        String modalidades = listaModalidades.stream().collect(Collectors.joining(","));
        menuBD.setModalidades(modalidades);

    }

    private void verificar(List<String> modalidades, String estadoModalidad, ModalidadEstudioEnum modalidaEnum) {
        if (StringUtils.isBlank(estadoModalidad)) {
            modalidades.remove(modalidaEnum.name());
            return;
        }
        EstadoEnum estado = EstadoEnum.valueOf(estadoModalidad);
        if (estado != EstadoEnum.ACT) {
            modalidades.remove(modalidaEnum.name());
        }
    }

    private void setEstadoMenu(Menu menuForm, Menu menuBD) {
        String ambiente = despliegueConfig.getAmbiente().toUpperCase();
        EstadoEnum estadoActual = getEstadoMenu(menuBD);
        EstadoEnum estadoForm = menuForm.getEstadoEnum();

        if (estadoActual != estadoForm) {
            List<String> entornosList = new ArrayList(Arrays.asList(menuBD.getEntornos().split(",")));

            if (estadoForm == EstadoEnum.ACT) {
                entornosList.add(ambiente);

            } else if (estadoForm == EstadoEnum.INA) {
                entornosList.remove(ambiente);
            }
            String entornos = entornosList.stream().collect(Collectors.joining(","));
            menuBD.setEntornos(entornos);
        }
    }

    private EstadoEnum getEstadoMenu(Menu menu) {
        String ambiente = despliegueConfig.getAmbiente().toUpperCase();
        String entornos = menu.getEntornos();

        if (entornos.contains(ambiente)) {
            return EstadoEnum.ACT;
        }
        return EstadoEnum.INA;
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
        menus.stream().forEachOrdered(menu -> {
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
            log.debug("{}", menuRol.getMenu().getId());
            log.debug("{}", menuRol.getRol().getId());

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
            menus = menuDAO.allByTipo(MenuTipoEnum.TITULO, new Sistema(GlobalConstantine.ID_SISTEMA_MAIPI));
        } else {
            menus = menuDAO.allBySuperMenu(new Sistema(GlobalConstantine.ID_SISTEMA_MAIPI), menuDb.getMenuSuperior());
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
            menuOrdenRequerido = menuDAO.findByTipoOrden(MenuTipoEnum.TITULO, new Sistema(GlobalConstantine.ID_SISTEMA_MAIPI), ordenup);
        } else {
            menuOrdenRequerido = menuDAO.findBySuperMenuOrden(new Sistema(GlobalConstantine.ID_SISTEMA_MAIPI), menuDb.getMenuSuperior(), ordenup);
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
            menus = menuDAO.allByTipo(MenuTipoEnum.TITULO, new Sistema(GlobalConstantine.ID_SISTEMA_MAIPI));
        } else {
            menus = menuDAO.allBySuperMenu(new Sistema(GlobalConstantine.ID_SISTEMA_MAIPI), menuDb.getMenuSuperior());
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
            menuOrdenRequerido = menuDAO.findByTipoOrden(MenuTipoEnum.TITULO, new Sistema(GlobalConstantine.ID_SISTEMA_MAIPI), ordendown);
        } else {
            menuOrdenRequerido = menuDAO.findBySuperMenuOrden(new Sistema(GlobalConstantine.ID_SISTEMA_MAIPI), menuDb.getMenuSuperior(), ordendown);
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
        Sistema sistema = new Sistema(GlobalConstantine.ID_SISTEMA_MAIPI);
        String entorno = despliegueConfig.getAmbiente().toUpperCase();
        List<MenuRol> menusRoles = menuRolDAO.allBySistemaEntorno(sistema, entorno);
        for (MenuRol mr : menusRoles) {
            Menu menu = mr.getMenu();
            System.out.println("menu=" + menu.getClave() + " entorno=" + menu.getEntornos() + " nombre=" + menu.getNombre());
        }
        visorMenu.setMenusRoles(menusRoles);
    }

}
