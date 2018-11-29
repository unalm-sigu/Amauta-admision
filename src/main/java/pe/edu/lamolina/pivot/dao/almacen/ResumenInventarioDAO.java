package pe.edu.lamolina.pivot.dao.almacen;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.almacen.ResumenInventario;
import pe.edu.lamolina.model.general.Aula;

public interface ResumenInventarioDAO extends EasyDAO<ResumenInventario> {

    public List<ResumenInventario> allByDynatable(DynatableFilter filter, Aula aula);

}
