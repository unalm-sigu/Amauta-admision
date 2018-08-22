package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.FormularioEstadoTramite;
import pe.edu.lamolina.model.tramite.TipoTramite;

public interface FormularioEstadoTramiteDAO extends EasyDAO<FormularioEstadoTramite> {

    FormularioEstadoTramite findByTipoTramiteAndEstadoTramite(TipoTramite tipoTramite, EstadoTramite estadoTramite);

    List<FormularioEstadoTramite> allByTipoTramiteAndEstadoTramite(TipoTramite tipoTramite, EstadoTramite estadoTramite);

    List<FormularioEstadoTramite> all();

}
