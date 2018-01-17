package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Ubicacion;

public interface UbicacionDAO extends EasyDAO<Ubicacion> {

    List<Ubicacion> allDistritos(String nombre);

}
