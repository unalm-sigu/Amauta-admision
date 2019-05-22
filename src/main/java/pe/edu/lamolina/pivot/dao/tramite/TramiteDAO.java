package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.tramite.Tramite;

public interface TramiteDAO extends EasyDAO<Tramite> {

    List<Tramite> allByFilter(DynatableFilter filter);

    List<Tramite> allByTipoTramiteEstadoTramite(TipoTramiteEnum tipoTramiteEnum, EstadoTramiteEnum estadoTramiteEnum);

    void updateEstado(Tramite tramite);

    void updateObservacion(Tramite tramite);

    Tramite find(Long id);

    public Tramite findById(Tramite tramite);

    public List<Tramite> allReiAndRetByCiclo(CicloAcademico cicloAcademico, DynatableFilter filter);

}
