package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.model.bienestar.TipoSubvencion;
import pe.edu.lamolina.model.tramite.AccionTramiteBienestar;
import pe.edu.lamolina.pivot.dao.tramite.AccionTramiteBienestarDAO;

@Repository
public class AccionTramiteBienestarDAOH extends AbstractEasyDAO<AccionTramiteBienestar> implements AccionTramiteBienestarDAO {

    public AccionTramiteBienestarDAOH() {
        super();
        setClazz(AccionTramiteBienestar.class);
    }

    @Override
    public AccionTramiteBienestar findByTipoSubvencion(TipoSubvencion tipoSubvencion, String estadoInicio, String respuesta) {
        Octavia sql = new Octavia()
                .from(AccionTramiteBienestar.class, "atc")
                .join("tipoTramite", "tipoSubvencion ts")
                .filter("ts.id", tipoSubvencion)
                .filter("estadoInicio", estadoInicio)
                .filter("respuesta", respuesta);

        return find(sql);
    }

    @Override
    public List<AccionTramiteBienestar> allNextByEstadoInicio(TipoSubvencion tipoSubvencion, String estadoInicio) {
        Octavia sql = new Octavia()
                .from(AccionTramiteBienestar.class, "atb")
                .join("tipoSubvencion ts")
                .filter("ts.id", tipoSubvencion)
                .filter("estadoInicio", estadoInicio);
        return all(sql);
    }
}
