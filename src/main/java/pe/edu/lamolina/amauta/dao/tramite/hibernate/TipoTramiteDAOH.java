package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import pe.edu.lamolina.amauta.dao.tramite.TipoTramiteDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.TipoTramite;

@Repository
public class TipoTramiteDAOH extends AbstractEasyDAO<TipoTramite> implements TipoTramiteDAO {

    public TipoTramiteDAOH() {
        super();
        setClazz(TipoTramite.class);
    }

    @Override
    public TipoTramite findByCodigo(String tipoTramite) {
        Octavia sql = Octavia.query()
                .from(TipoTramite.class, "tt")
                .join("oficina")
                .filter("tt.codigo", tipoTramite);
        return find(sql);
    }
}
