package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.dao.tramite.ReincorporacionDAO;

@Repository
public class ReincorporacionDAOH extends AbstractEasyDAO<Reincorporacion> implements ReincorporacionDAO {

    public ReincorporacionDAOH() {
        super();
        setClazz(Reincorporacion.class);
    }

    @Override
    public List<Reincorporacion> allByTramite(Tramite tramite) {
        Octavia sql = Octavia.query()
                .from(Reincorporacion.class, "rei")
                .join("tramite tra", "facultad fac", "estadoTramite et", "cicloReincorporacion cr")
                .filter("tra.id", tramite);

        return all(sql);
    }

    @Override
    public List<Reincorporacion> allByDyna(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Reincorporacion.class, "rei")
                .join("tramite tra", "resolucion res", "facultad fac", "estadoTramite et", "cicloReincorporacion cr")
                .join("tra.persona traPer", "tra.tipoTramite");
        return this.all(sql);
    }

    @Override
    public List<Reincorporacion> allByResolucion(Resolucion resolucion) {
        Octavia sql = Octavia.query()
                .from(Reincorporacion.class, "rei")
                .join("tramite tra", "resolucion res", "facultad fac", "estadoTramite et", "cicloReincorporacion cr")
                .filter("res.id", resolucion);

        return all(sql);
    }

    @Override
    public Reincorporacion findByTramiteEstadoTram(Tramite tramite, EstadoTramiteEnum estadoTramiteEnum) {
        Octavia sql = Octavia.query()
                .from(Reincorporacion.class, "rei")
                .join("tramite tra", "facultad fac", "estadoTramite et", "cicloReincorporacion cr")
                .filter("tra.id", tramite)
                .filter("et.id", estadoTramiteEnum.getId());

        return (Reincorporacion) sql.find(getCurrentSession());
    }

    @Override
    public void updateEstado(Reincorporacion reincorporacion) {
        Octavia octavia = Octavia.update(Reincorporacion.class);
        octavia.set(reincorporacion, "estadoTramite");
        this.update(octavia);
    }

    @Override
    public void updateAceptado(Reincorporacion reincorporacion) {
        Octavia octavia = Octavia.update(Reincorporacion.class);
        octavia.set(reincorporacion, "aceptado");
        octavia.set(reincorporacion, "resolucion.id");
        this.update(octavia);
    }

}
