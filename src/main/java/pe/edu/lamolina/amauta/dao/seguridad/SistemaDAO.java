package pe.edu.lamolina.amauta.dao.seguridad;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Sistema;

public interface SistemaDAO extends EasyDAO<Sistema> {

    Sistema findByRolSistema(Rol rol, Sistema sys);

    List<Sistema> allByCodes(List<String> codigos);

}
