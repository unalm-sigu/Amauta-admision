package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.FormularioEstadoTramite;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.amauta.dao.tramite.FormularioEstadoTramiteDAO;

@Repository
public class FormularioEstadoTramiteDAOH extends AbstractEasyDAO<FormularioEstadoTramite> implements FormularioEstadoTramiteDAO {

    public FormularioEstadoTramiteDAOH() {
        super();
        setClazz(FormularioEstadoTramite.class);
    }

    @Override
    public FormularioEstadoTramite findByTipoTramiteAndEstadoTramite(TipoTramite tipoTramite, EstadoTramite estadoTramite) {
        Octavia sql = Octavia.query()
                .from(FormularioEstadoTramite.class, "sec")
                .join("tipoTramite tt", "estadoTramite et")
                .filter("tt.id", tipoTramite)
                .filter("et.id", estadoTramite);
        return find(sql);
    }

    @Override
    public List<FormularioEstadoTramite> allByTipoTramiteAndEstadoTramite(TipoTramite tipoTramite, EstadoTramite estadoTramite) {
        Octavia sql = Octavia.query()
                .from(FormularioEstadoTramite.class, "sec")
                .join("tipoTramite tt", "estadoTramite et")
                .filter("tt.id", tipoTramite)
                .filter("et.id", estadoTramite);
        return all(sql);
    }

    @Override
    public List<FormularioEstadoTramite> all() {
        Octavia sql = Octavia.query()
                .from(FormularioEstadoTramite.class, "sec")
                .join("tipoTramite tt", "estadoTramite et");
        return all(sql);
    }

}
