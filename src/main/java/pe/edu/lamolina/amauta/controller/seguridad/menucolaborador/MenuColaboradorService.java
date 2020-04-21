package pe.edu.lamolina.amauta.controller.seguridad.menucolaborador;

import java.util.List;
import pe.edu.lamolina.model.enums.MenuTipoEnum;
import pe.edu.lamolina.model.seguridad.ColaboradorMenu;
import pe.edu.lamolina.model.seguridad.Menu;

public interface MenuColaboradorService {

    List<Menu> allMenuReportes(MenuTipoEnum menuTipoEnum);

    List<ColaboradorMenu> allMenuColaborador();

}
