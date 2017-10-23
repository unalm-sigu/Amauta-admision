package pe.edu.lamolina.pivot.dao.seguridad;

import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.seguridad.Rol;
import pe.edu.lamolina.pivot.model.seguridad.RolSistema;
import pe.edu.lamolina.pivot.model.seguridad.Sistema;

public interface RolSistemaDAO extends Crud<RolSistema> {

    RolSistema findByRolSistema(Rol rol, Sistema sistema);

}
