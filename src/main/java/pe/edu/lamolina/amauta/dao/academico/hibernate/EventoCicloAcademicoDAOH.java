package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import pe.edu.lamolina.model.enums.TipoEventoAcademicoEnum;
import pe.edu.lamolina.amauta.dao.academico.EventoCicloAcademicoDAO;

@Repository
public class EventoCicloAcademicoDAOH extends AbstractEasyDAO<EventoCicloAcademico> implements EventoCicloAcademicoDAO {

    public EventoCicloAcademicoDAOH() {
        super();
        setClazz(EventoCicloAcademico.class);
    }

    @Override
    public List<EventoCicloAcademico> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(EventoCicloAcademico.class, "eca")
                .join("cicloAcademico ca", "eventoAcademico ea")
                .leftJoin("color")
                .filter("ca.id", cicloAcademico)
                .searchFields("ea.codigo", "ea.nombre")
                .orderBy("eca.fechaFin desc");
        sql.beginRelativeFilters();
        return sql.all(getCurrentSession());
    }

    @Override
    public EventoCicloAcademico findEventoCicloAcademico(EventoCicloAcademico eventoCicloAcademico) {
        Octavia sql = Octavia.query()
                .from(EventoCicloAcademico.class, "eca")
                .join("cicloAcademico ca", "eventoAcademico ea")
                .filter("eca.id", eventoCicloAcademico);
        return (EventoCicloAcademico) sql.find(getCurrentSession());
    }

    @Override
    public List<EventoCicloAcademico> allcalendar(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(EventoCicloAcademico.class, "eca")
                .join("cicloAcademico ca", "eventoAcademico ea")
                .left("color")
                .filter("ca.id", cicloAcademico);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<EventoCicloAcademico> allEventosMatriculaByCiclo(CicloAcademico ciclo) {

        Octavia sql = Octavia.query()
                .from(EventoCicloAcademico.class, "eca")
                .join("eventoAcademico ea", "cicloAcademico ca")
                .filter("ca.id", ciclo)
                .filter("ea.tipo", "MAT");

        return all(sql);
    }

    @Override
    public List<EventoCicloAcademico> allEventoAcademicoByCicloAca(CicloAcademico cicloAcademico) {

        Octavia sql = Octavia.query()
                .from(EventoCicloAcademico.class, "eca")
                .join("eventoAcademico", "cicloAcademico ca")
                .leftJoin("color")
                .filter("ca.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public List<EventoCicloAcademico> allActivosByCicloEventos(CicloAcademico cicloAcademico, List<EventoAcademicoEnum> eventoAcademicos) {
        Octavia sql = Octavia.query()
                .from(EventoCicloAcademico.class, "eca")
                .join("eventoAcademico ea")
                .in("ea.codigo", eventoAcademicos);
        return all(sql);
    }

    @Override
    public EventoCicloAcademico findActivoByCicloTipoEvento(CicloAcademico cicloAcademico, EventoAcademicoEnum eventoAcademicoEnum) {
        Octavia sql = Octavia.query()
                .from(EventoCicloAcademico.class, "eca")
                .join("eventoAcademico ea", "cicloAcademico ca")
                .filter("ea.codigo", eventoAcademicoEnum)
                .filter("ca.id", cicloAcademico);
        return find(sql);
    }

    @Override
    public List<EventoCicloAcademico> allEventoExamenByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(EventoCicloAcademico.class, "eca")
                .join("eventoAcademico ea", "cicloAcademico ca")
                .left("color col")
                .filter("eca.estado", EstadoEnum.CRE)
                .filter("ea.tipo", TipoEventoAcademicoEnum.EXAMEN)
                .filter("eca.cicloAcademico", cicloAcademico);
        return all(sql);
    }

    @Override
    public List<EventoCicloAcademico> allActivoByCicloTipoEvento(List<CicloAcademico> cicloAcademicos, EventoAcademicoEnum eventoAcademicoEnum) {
        Octavia sql = Octavia.query()
                .from(EventoCicloAcademico.class, "eca")
                .join("eventoAcademico ea", "cicloAcademico ca")
                .filter("ea.codigo", eventoAcademicoEnum)
                .in("ca.id", cicloAcademicos);
        return all(sql);
    }

}
