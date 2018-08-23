package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.bienestar.TipoSubvencion;
import pe.edu.lamolina.model.tramite.AccionTramiteBienestar;

public interface AccionTramiteBienestarDAO extends EasyDAO<AccionTramiteBienestar> {

    AccionTramiteBienestar findByTipoSubvencion(TipoSubvencion tipoSubvencion, String estadoInicio, String respuesta);

    List<AccionTramiteBienestar> allNextByEstadoInicio(TipoSubvencion tipoSubvencion, String string);

}
