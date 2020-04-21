package pe.edu.lamolina.amauta.dao.almacen;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.almacen.Almacen;
import pe.edu.lamolina.model.almacen.Producto;
import pe.edu.lamolina.model.almacen.ResumenInventario;
import pe.edu.lamolina.model.general.Aula;

public interface ResumenInventarioDAO extends EasyDAO<ResumenInventario> {

    List<ResumenInventario> allByDynatable(DynatableFilter filter, Aula aula);

    ResumenInventario findByAlmacenProducto(Almacen almacen, Producto producto);

    List<ResumenInventario> allVisiblesByAulas(List<Aula> aulas);

}
