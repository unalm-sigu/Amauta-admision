package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.AccionTramiteAcademico;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.amauta.dao.tramite.AccionTramiteAcademicoDAO;

@Repository
public class AccionTramiteAcademicoDAOH extends AbstractEasyDAO<AccionTramiteAcademico> implements AccionTramiteAcademicoDAO {

    public AccionTramiteAcademicoDAOH() {
        super();
        setClazz(AccionTramiteAcademico.class);
    }

    @Override
    public AccionTramiteAcademico find(long id) {
        Octavia sql = Octavia.query()
                .from(AccionTramiteAcademico.class, "a")
                .join("tipoTramite tt", "estadoTramiteInicio eti", "estadoTramiteFinal etf")
                .leftJoin("oficinaOrigen oo", "tipoOficinaOrigen too", "oficinaDestino od", "tipoOficinaDestino tod")
                .filter("a.id", id);
        return find(sql);
    }

    @Override
    public List<AccionTramiteAcademico> allByTipoTramiteAndEstadoTramiteInicial(TipoTramite tipoTramite, EstadoTramite estadoTramiteInicial) {
        Octavia sql = Octavia.query()
                .from(AccionTramiteAcademico.class, "sec")
                .join("tipoTramite tt", "estadoTramiteInicio eti", "estadoTramiteFinal etf")
                .leftJoin("oficinaOrigen oo", "tipoOficinaOrigen too", "oficinaDestino od", "tipoOficinaDestino tod")
                .filter("tt.id", tipoTramite)
                .filter("eti.id", estadoTramiteInicial);
        return all(sql);
    }

    public List<AccionTramiteAcademico> all(TipoTramite tipoTramite, EstadoTramite estadoTramiteInicial) {
        Octavia sql = Octavia.query()
                .from(AccionTramiteAcademico.class, "sec")
                .join("tipoTramite tt", "estadoTramiteInicio eti", "estadoTramiteFinal etf")
                .leftJoin("oficinaOrigen oo", "tipoOficinaOrigen too", "oficinaDestino od", "tipoOficinaDestino tod");
        return all(sql);
    }

    @Override
    public List<AccionTramiteAcademico> allByTipoTramite(TipoTramite tipoTramite) {
        Octavia sql = Octavia.query()
                .from(AccionTramiteAcademico.class, "sec")
                .join("tipoTramite tt", "estadoTramiteInicio eti", "estadoTramiteFinal etf")
                .leftJoin("oficinaOrigen oo", "tipoOficinaOrigen too", "oficinaDestino od", "tipoOficinaDestino tod")
                .filter("tt.id", tipoTramite);
        return all(sql);
    }

}
