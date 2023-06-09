package pe.edu.lamolina.amauta.controller.general.producto;

import pe.albatross.octavia.dynatable.DynatableFilter;

import pe.edu.lamolina.model.almacen.Producto;

import java.util.List;

public interface ProductoService {
    List<Producto> allProductosByAulas();
//    List<Producto> allByDynatable(DynatableFilter filter);
}
