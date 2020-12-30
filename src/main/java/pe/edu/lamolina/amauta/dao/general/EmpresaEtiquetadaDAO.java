package pe.edu.lamolina.amauta.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.EmpresaEtiquetada;

public interface EmpresaEtiquetadaDAO extends EasyDAO<EmpresaEtiquetada> {

    List<EmpresaEtiquetada> allBancos();

}
