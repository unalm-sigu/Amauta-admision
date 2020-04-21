package pe.edu.lamolina.amauta.dao.seguridad;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.MenuTipoEnum;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Sistema;

public interface MenuDAO extends EasyDAO<Menu> {

    List<Menu> allByRolSistema(List<Rol> roles, Sistema sistema);

    List<Menu> allMenuSystem(Sistema sistema);

    Integer getMayorOrden(Sistema sistema);

    List<Menu> allByTipo(MenuTipoEnum menuTipoEnum, Sistema sistema);

    List<Menu> allBySuperMenu(Sistema sistema, Menu menuSuperior);

    Menu findByTipoOrden(MenuTipoEnum menuTipoEnum, Sistema sistema, Integer orden);

    Menu findBySuperMenuOrden(Sistema sistema, Menu menuSuperior, Integer orden);

    Integer getMayorOrdenGrupo(Sistema sistema, Menu menuSuperior);

    Integer getMayorOrdenTipo(Sistema sistema, MenuTipoEnum menuTipoEnum);

    List<Menu> allMenuSystemByRol(Sistema sistema, Long idRol);

    Menu findByRuta(String reporte);

}
