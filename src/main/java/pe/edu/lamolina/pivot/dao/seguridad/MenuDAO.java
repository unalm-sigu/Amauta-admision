package pe.edu.lamolina.pivot.dao.seguridad;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.seguridad.Menu;
import pe.edu.lamolina.pivot.model.seguridad.Rol;
import pe.edu.lamolina.pivot.model.seguridad.Sistema;
import pe.edu.lamolina.pivot.zelper.enums.MenuTipoEnum;

public interface MenuDAO extends Crud<Menu> {

    List<Menu> allMenuRolActivo(Rol rolAsignar);

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
