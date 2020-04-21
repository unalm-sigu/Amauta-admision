package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.EventoAcademico;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import pe.edu.lamolina.amauta.dao.academico.EventoAcademicoDAO;

@Repository
public class EventoAcademicoDAOH extends AbstractEasyDAO<EventoAcademico> implements EventoAcademicoDAO {

    public EventoAcademicoDAOH() {
        super();
        setClazz(EventoAcademico.class);
    }

    @Override
    public List<EventoAcademico> allEventoAcademicoByName(String nombre) {
        Octavia sql = Octavia.query()
                .from(EventoAcademico.class, "ea")
                .beginBlock()
                .__().filter("ea.nombre", "like", nombre)
                .__().filter("ea.codigo", "like", nombre)
                .__().filter("ea.tipo", "like", nombre)
                .endBlock()
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public EventoAcademico findByCodigo(EventoAcademicoEnum eventoAcademicoEnum) {
        Octavia sql = Octavia.query()
                .from(EventoAcademico.class, "ea")
                .filter("ea.codigo", eventoAcademicoEnum.name());

        return find(sql);
    }

}
