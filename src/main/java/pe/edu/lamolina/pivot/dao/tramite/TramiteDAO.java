package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.tramite.Tramite;

public interface TramiteDAO extends EasyDAO<Tramite> {

    List<Tramite> allByFilter(DynatableFilter filter);

    List<Tramite> allByTipoTramiteEstadoTramite(TipoTramiteEnum tipoTramiteEnum, EstadoTramiteEnum estadoTramiteEnum);

    void updateEstado(Tramite tramite);

    Tramite find(Long id);

}
