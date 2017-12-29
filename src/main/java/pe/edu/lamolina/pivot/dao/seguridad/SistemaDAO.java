package pe.edu.lamolina.pivot.dao.seguridad;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Sistema;

public interface SistemaDAO extends EasyDAO<Sistema> {

    Sistema findByRolSistema(Rol rol, Sistema sys);

}
