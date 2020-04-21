package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.TipoRepitencia;

public interface TipoRepitenciaDAO extends EasyDAO<TipoRepitencia> {

    List<TipoRepitencia> allByCode(List<String> codigos);

}
