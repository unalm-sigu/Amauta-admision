package pe.edu.lamolina.amauta.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.inscripcion.ContenidoVariable;

public interface ContenidoVariableDAO extends EasyDAO<ContenidoVariable> {

    List<ContenidoVariable> allByContenidoId(Long idContenido);

}
