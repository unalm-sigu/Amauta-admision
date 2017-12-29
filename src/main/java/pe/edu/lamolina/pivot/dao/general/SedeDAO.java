package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Sede;

public interface SedeDAO extends EasyDAO<Sede> {

    List<Sede> allSedesByName(String nombre);

}
