package pe.edu.lamolina.pivot.dao.seguridad;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.seguridad.PerfilRol;

public interface PerfilRolDAO extends EasyDAO<PerfilRol> {

    List<PerfilRol> allByPerfilCompania(PerfilCompania perfilCompania);

}
