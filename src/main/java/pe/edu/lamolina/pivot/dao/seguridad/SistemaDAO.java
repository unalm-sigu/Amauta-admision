package pe.edu.lamolina.pivot.dao.seguridad;

import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.seguridad.Rol;
import pe.edu.lamolina.pivot.model.seguridad.Sistema;

public interface SistemaDAO extends Crud<Sistema> {

    Sistema findByRolSistema(Rol rol, Sistema sys);

}
