package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import pe.edu.lamolina.pivot.dao.inscripcion.EventoCicloDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Evento;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;

@Repository
public class EventoCicloDAOH extends AbstractEasyDAO<EventoCiclo> implements EventoCicloDAO {

    public EventoCicloDAOH() {
        super();
        setClazz(EventoCiclo.class);
    }

    @Override
    public EventoCiclo findByCicloFecha(CicloPostula ciclo, Date fecha) {

        Octavia sql = Octavia.query()
                .from(EventoCiclo.class, "evci")
                .join("evento ev", "cicloPostula ci")
                .filter("ci.id", ciclo)
                .filter("evci.fechaInicio", "<=", fecha)
                .filter("evci.fechaFin", ">", fecha);

        return (EventoCiclo) sql.find(getCurrentSession());
    }

    @Override
    public EventoCiclo findByInicioCiclo(CicloPostula ciclo, Date fecha) {
        Octavia sql = Octavia.query()
                .from(EventoCiclo.class, "evci")
                .join("evento ev", "cicloPostula ci")
                .filter("ci.id", ciclo)
                .isNull("evci.fechaInicio")
                .filter("evci.fechaFin", ">", fecha);

        return (EventoCiclo) sql.find(getCurrentSession());
    }

    @Override
    public EventoCiclo findByFinCiclo(CicloPostula ciclo, Date fecha) {
        Octavia sql = Octavia.query()
                .from(EventoCiclo.class, "evci")
                .join("evento ev", "cicloPostula ci")
                .filter("ci.id", ciclo)
                .isNull("evci.fechaFin")
                .filter("evci.fechaInicio", "<=", fecha);

        return (EventoCiclo) sql.find(getCurrentSession());
    }

    @Override
    public List<EventoCiclo> allByFilterCiclo(DynatableFilter filter, CicloPostula ciclo) {

        DynatableSql sql = new DynatableSql(filter)
                .from(EventoCiclo.class, "ec")
                .join("evento ev", "cicloPostula cp")
                .filter("cp.id", ciclo)
                .searchFields("ec.fechaInicio", "ec.fechaFin", "ev.codigo", "ev.descripcion")
                .orderBy("coalesce(ec.fechaInicio,ec.fechaFin)");

        return sql.all(getCurrentSession());
    }

    @Override
    public List<EventoCiclo> allByEventoCiclo(Evento evento, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(EventoCiclo.class, "evci")
                .join("evento ev", "cicloPostula ci")
                .filter("ci.id", ciclo)
                .filter("ev.id", evento);

        return sql.all(getCurrentSession());
    }

    @Override
    public EventoCiclo find(long id) {
        Octavia sql = Octavia.query()
                .from(EventoCiclo.class, "evci")
                .join("evento ev", "cicloPostula ci")
                .filter("evci.id", id);

        return (EventoCiclo) sql.find(getCurrentSession());
    }

    @Override
    public List<EventoCiclo> allByCiclo(CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(EventoCiclo.class, "ec")
                .join("cicloPostula ci", "evento")
                .filter("ci.id", ciclo.getId());
        return sql.all(getCurrentSession());
    }

    @Override
    public List<EventoCiclo> allInscripcionesByCiclo(CicloPostula ciclo) {
        Octavia sql = Octavia.query(EventoCiclo.class, "evci")
                .join("evento ev", "cicloPostula ci")
                .filter("ci.id", ciclo)
                .in("ev.codigo", Arrays.asList("CEPRE", "INSC", "OTR", "EXTM"));

        return all(sql);
    }

    @Override
    public EventoCiclo findExtemporaneoByFecha(CicloPostula ciclo, Date fecha) {
        Octavia sql = Octavia.query(EventoCiclo.class, "evci")
                .join("evento ev", "cicloPostula ci")
                .filter("ev.codigo", "EXTM")
                .filter("ci.id", ciclo)
                .filter("evci.fechaInicio", fecha);

        return find(sql);
    }

}

