package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.PerfilCompania;

public interface PerfilCompaniaDAO extends EasyDAO<PerfilCompania> {

    List<PerfilCompania> allByNombre(String nombre);

}
