package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.VariableGenerica;

public interface VariableGenericaDAO extends EasyDAO<VariableGenerica> {

    public List<VariableGenerica> allByCodigo(List<String> listVariable);

}
