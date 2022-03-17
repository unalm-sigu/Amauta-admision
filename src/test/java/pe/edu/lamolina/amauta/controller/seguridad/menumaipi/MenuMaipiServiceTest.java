package pe.edu.lamolina.amauta.controller.seguridad.menumaipi;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import pe.albatross.zelpers.json.JaneHelper;
import pe.edu.lamolina.amauta.config.DespliegueConfig;
import pe.edu.lamolina.amauta.controller.seguridad.menu.VisorMenu;
import pe.edu.lamolina.amauta.dao.seguridad.MenuDAO;
import pe.edu.lamolina.amauta.dao.seguridad.MenuRolDAO;
import pe.edu.lamolina.amauta.dao.seguridad.RolDAO;
import pe.edu.lamolina.amauta.dao.seguridad.RolSistemaDAO;
import pe.edu.lamolina.amauta.dao.seguridad.SistemaDAO;
import pe.edu.lamolina.model.enums.MenuTipoEnum;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Sistema;

@Slf4j
public class MenuMaipiServiceTest {

    @Mock
    private MenuDAO menuDAO;
    @Mock
    private RolDAO rolDAO;
    @Mock
    private MenuRolDAO menuRolDAO;
    @Mock
    private RolSistemaDAO rolSistemaDAO;
    @Mock
    private SistemaDAO sistemaDAO;
    @Mock
    private VisorMenu visorMenu;
    @Mock
    private DespliegueConfig despliegueConfig;

    private MenuMaipiService menuMaipiService;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        
        menuMaipiService = new MenuMaipiServiceImp(
            menuDAO,
            rolDAO,
            menuRolDAO,
            rolSistemaDAO,
            sistemaDAO,
            visorMenu,
            despliegueConfig);
    }

    @Test
    public void findMenu_Test() {
        Menu mm = this.createMenuPregrado();

        when(menuDAO.find(any(Long.class))).thenReturn(mm);
        when(menuDAO.allBySuperMenu(any(Sistema.class), any(Menu.class))).thenReturn(new ArrayList());
        when(despliegueConfig.getAmbiente()).thenReturn("PROD");
        
        Menu menu = menuMaipiService.find(new Menu(11111L));
        
        ObjectNode json = JaneHelper.from(menu).json();
        log.debug(json.toString());
        
    }

    private Menu createMenuPregrado() {
        Menu menu = new Menu();
        menu.setId(34L);
        menu.setSistema(new Sistema(4L));
        menu.setMenuSuperior(new Menu(33L));
        menu.setNombre("Avance Curricular");
        menu.setIcono("fa-ambulance");
        menu.setRuta("/academico/avancecurricular");
        menu.setTipoEnum(MenuTipoEnum.MENU);
        menu.setOrden(4);
        menu.setClave("XU3O10MX2TX4G5MCKZO6");
        menu.setEntornos("PROD,TEST,DESA");
        menu.setModalidades("PRE");

        return menu;
    }
}
