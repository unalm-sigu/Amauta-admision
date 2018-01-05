package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Universidad;

public interface UniversidadDAO extends EasyDAO<Universidad> {

    List<Universidad> allUniversidadByName(String nombre);

}
