package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.amauta.dao.tramite.EstadoTramiteDAO;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;

@Repository
public class EstadoTramiteDAOH extends AbstractEasyDAO<EstadoTramite> implements EstadoTramiteDAO {

    public EstadoTramiteDAOH() {
        super();
        setClazz(EstadoTramite.class);
    }

    @Override
    public EstadoTramite findByCodigoEnum(TramiteEstadoEnum codigoEnum) {
        Octavia sql = new Octavia()
                .from(EstadoTramite.class, "et")
                .filter("codigo", codigoEnum.name());
        return find(sql);
    }

}
