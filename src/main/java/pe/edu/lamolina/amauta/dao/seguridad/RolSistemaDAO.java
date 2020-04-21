package pe.edu.lamolina.amauta.dao.seguridad;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.RolSistema;
import pe.edu.lamolina.model.seguridad.Sistema;

public interface RolSistemaDAO extends EasyDAO<RolSistema> {

    RolSistema findByRolSistema(Rol rol, Sistema sistema);

    List<RolSistema> allByRoles(List<Rol> roles);

}
