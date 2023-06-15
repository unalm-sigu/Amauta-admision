package pe.edu.lamolina.amauta.dao.almacen;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.almacen.Almacen;
import pe.edu.lamolina.model.almacen.Inventario;
import pe.edu.lamolina.model.general.Aula;

import javax.persistence.criteria.CriteriaBuilder;

public interface InventarioDAO extends EasyDAO<Inventario> {

    public List<Inventario> allByDynatable(DynatableFilter filter, Aula aula);

    public List<Inventario> allByDynatable(DynatableFilter filter);

    public List<Inventario> allById(List<Inventario> inventarios);
    public List<Inventario> findByAlmacen(Almacen almacen);
    public Inventario findLastCodeInventarioByOficina();

}
