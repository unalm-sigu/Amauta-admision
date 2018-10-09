package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.bienestar.TipoSubvencion;
import pe.edu.lamolina.model.tramite.AccionTramiteBienestar;
import pe.edu.lamolina.model.tramite.AccionTramiteDocumento;

public interface AccionTramiteDocumentoDAO extends EasyDAO<AccionTramiteDocumento> {


    List<AccionTramiteDocumento> allNextByEstadoInicio(TipoSubvencion tipoSubvencion, String string);

}
