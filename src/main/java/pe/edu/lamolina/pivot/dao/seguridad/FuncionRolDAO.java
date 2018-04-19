package pe.edu.lamolina.pivot.dao.seguridad;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.seguridad.FuncionRol;

public interface FuncionRolDAO extends EasyDAO<FuncionRol> {

     List<FuncionRol> allByPerfilCompania(List<PerfilCompania> perfilCompania);
}
