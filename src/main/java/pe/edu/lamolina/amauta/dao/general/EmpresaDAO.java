package pe.edu.lamolina.amauta.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Empresa;
import pe.edu.lamolina.model.general.Pais;

public interface EmpresaDAO extends EasyDAO<Empresa> {

    public List<Empresa> allEmpresaByName(Pais pais, String nombre);

}
