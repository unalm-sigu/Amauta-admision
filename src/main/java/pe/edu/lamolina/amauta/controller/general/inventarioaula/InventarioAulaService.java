package pe.edu.lamolina.amauta.controller.general.inventarioaula;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.almacen.Inventario;
import pe.edu.lamolina.model.almacen.Producto;
import pe.edu.lamolina.model.almacen.ResumenInventario;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface InventarioAulaService {

    Aula findAula(Long idaula);

    List<Inventario> allByDynatable(DynatableFilter filter, Aula aula);

    void update(Inventario inventario, Usuario user);

    void save(Inventario inventario, Usuario user);

    void delete(Inventario inventario);

    Inventario find(Inventario inventario);

    List<Producto> allProducto();

    void saveProducto(Producto producto, Usuario user);

    List<ResumenInventario> allResumenByDynatable(DynatableFilter filter, Aula aula);

    void updateResumen(ResumenInventario resumen);

    void updateInventarioCode(List<Inventario> inventarios, Usuario user);

}
