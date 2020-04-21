package pe.edu.lamolina.amauta.dao.sip;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.inscripcion.ContenidoCartaVariable;

public interface ContenidoCartaVariableDAO extends EasyDAO<ContenidoCartaVariable> {

    List<ContenidoCartaVariable> allByIdContenido(Long idContenido);

}
