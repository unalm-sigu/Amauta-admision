package pe.edu.lamolina.pivot.controller.seguridad.menucolaborador;

import java.util.List;
import pe.edu.lamolina.pivot.model.seguridad.ColaboradorMenu;
import pe.edu.lamolina.pivot.model.seguridad.Menu;
import pe.edu.lamolina.pivot.zelper.enums.MenuTipoEnum;

public interface MenuColaboradorService {

    List<Menu> allMenuReportes(MenuTipoEnum menuTipoEnum);

    List<ColaboradorMenu> allMenuColaborador();

}
