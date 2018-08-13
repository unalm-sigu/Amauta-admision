package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.TramiteReunionConsejo;
import pe.edu.lamolina.model.tramite.ReunionConsejo;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;

public interface TramiteReunionConsejoDAO extends EasyDAO<TramiteReunionConsejo> {

    public List<TramiteReunionConsejo> allByReunionConsejoAndTipoTramite(ReunionConsejo reunionConsejo, TipoTramite tipoTramite);

    TramiteReunionConsejo findByTramite(Tramite tramite);
}
