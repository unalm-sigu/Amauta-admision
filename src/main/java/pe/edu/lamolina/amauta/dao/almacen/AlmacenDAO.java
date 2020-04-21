package pe.edu.lamolina.amauta.dao.almacen;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.almacen.Almacen;
import pe.edu.lamolina.model.general.Aula;

public interface AlmacenDAO extends EasyDAO<Almacen> {

    public Almacen findByAula(Aula aula);

}
