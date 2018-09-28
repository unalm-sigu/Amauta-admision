package pe.edu.lamolina.pivot.dao.permisoprogramacion;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.permisoprogramacion.PermisoProgramacion;

public interface PermisoProgramacionDAO extends EasyDAO<PermisoProgramacion> {

    List<PermisoProgramacion> allPermisos();
}
