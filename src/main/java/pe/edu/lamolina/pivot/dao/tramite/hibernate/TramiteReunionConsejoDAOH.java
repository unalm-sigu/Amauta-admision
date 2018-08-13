package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.tramite.TramiteReunionConsejo;
import pe.edu.lamolina.model.tramite.ReunionConsejo;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.dao.tramite.TramiteReunionConsejoDAO;

@Repository
public class TramiteReunionConsejoDAOH extends AbstractEasyDAO<TramiteReunionConsejo> implements TramiteReunionConsejoDAO {

    public TramiteReunionConsejoDAOH() {
        super();
        setClazz(TramiteReunionConsejo.class);
    }

    @Override
    public List<TramiteReunionConsejo> allByReunionConsejoAndTipoTramite(ReunionConsejo reunionConsejo, TipoTramite tipoTramite) {
        Octavia sql = Octavia.query()
                .from(TramiteReunionConsejo.class, "arc")
                .join("reunionConsejo rc", "tramite tram")
                .join("tram.alumno alu", "alu.persona per", "tram.tipoTramite tt")
                .left("arc.userRegistro ur", "arc.userActualizacion ua")
                .left("tram.reincorporaciones")
                .filter("rc.id", reunionConsejo)
                .filter("tt.id", tipoTramite)
                .filter("arc.estado", EstadoEnum.ACT.name());
        return all(sql);
    }

    @Override
    public TramiteReunionConsejo findByTramite(Tramite tramite) {
        Octavia sql = Octavia.query()
                .from(TramiteReunionConsejo.class, "arc")
                .join("reunionConsejo rc", "tramite tram")
                .join("tram.alumno alu", "alu.persona per", "tram.tipoTramite tt")
                .left("arc.userRegistro ur", "arc.userActualizacion ua")
                .left("tram.reincorporaciones")
                .filter("tram.id", tramite)
                .filter("arc.estado", EstadoEnum.ACT.name());
        return find(sql);
    }

}
