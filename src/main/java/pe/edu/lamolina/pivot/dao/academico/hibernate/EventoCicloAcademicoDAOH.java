package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;

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

}
