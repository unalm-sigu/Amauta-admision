package pe.edu.lamolina.pivot.dao.almacen;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.almacen.Inventario;
import pe.edu.lamolina.model.general.Aula;

public interface InventarioDAO extends EasyDAO<Inventario> {

    public List<Inventario> allByDynatable(DynatableFilter filter, Aula aula);

    public List<Inventario> allById(List<Inventario> inventarios);

}
