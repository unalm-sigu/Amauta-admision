package pe.edu.lamolina.pivot.controller.seguridad.menucolaborador;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.enums.MenuTipoEnum;
import pe.edu.lamolina.model.seguridad.ColaboradorMenu;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.pivot.dao.seguridad.ColaboradorMenuDAO;
import pe.edu.lamolina.pivot.dao.seguridad.MenuDAO;

@Service
@Transactional(readOnly = true)
public class MenuColaboradorServiceImp implements MenuColaboradorService {

    @Autowired
    MenuDAO menuDAO;

    @Autowired
    ColaboradorMenuDAO colaboradorMenuDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<Menu> allMenuReportes(MenuTipoEnum menuTipoEnum) {
        return menuDAO.allByTipo(menuTipoEnum, new Sistema(1L));
    }

    @Override
    public List<ColaboradorMenu> allMenuColaborador() {
        List<ColaboradorMenu> menusCo = new ArrayList();
        Menu menuPadre = menuDAO.findByRuta("/reporte");

        Sistema sistema = new Sistema(1L);

        List<Menu> menus = menuDAO.allBySuperMenu(sistema, menuPadre);

        for (Menu menu : menus) {
            logger.debug("Menu {}", menu.getNombre());
        }

        return menusCo;
    }

}
