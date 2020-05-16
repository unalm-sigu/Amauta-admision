package pe.edu.lamolina.amauta.dao.tramite;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.tramite.EstadoTramite;

public interface EstadoTramiteDAO extends EasyDAO<EstadoTramite> {

    EstadoTramite findByCodigoEnum(TramiteEstadoEnum codigoEnum);

}
