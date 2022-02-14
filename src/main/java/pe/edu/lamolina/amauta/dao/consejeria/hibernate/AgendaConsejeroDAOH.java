package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AgendaConsejeroDAO;
import pe.edu.lamolina.model.consejeria.AgendaConsejero;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.consejeria.ReunionAlumnoConsejero;

@Service
public class AgendaConsejeroDAOH extends AbstractEasyDAO<AgendaConsejero> implements AgendaConsejeroDAO {

    public AgendaConsejeroDAOH() {
        super();
        setClazz(AgendaConsejero.class);
    }

    @Override
    public List<AgendaConsejero> allByConsejero(Consejero consejero) {
        Octavia sql = new Octavia()
                .from(AgendaConsejero.class, "ac")
                .join("consejero con", "hora hor")
                .filter("con.id", consejero);

        return all(sql);
    }

    @Override
    public List<AgendaConsejero> allDynatableByCicloAcademico(DynatableFilter filter) {

        DynatableSql sql = new DynatableSql(filter)
                .from(AgendaConsejero.class, "acon")
                .join("consejero con", "acon.hora hor", "con.colaborador cola")
                .join("con.carrera car", "car.facultad", "cola.persona per")
                .searchFields("acon.asunto", "acon.cuerpo", "acon.fecha")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("acon.fecha desc", "hor.id");

        this.setCondicion(filter, sql);

        return all(sql);

    }

    private void setCondicion(DynatableFilter filter, DynatableSql sql) {

        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }
        for (String key : queries.keySet()) {
            if (key.equals("search")) {
                continue;
            }
            String value = (String) queries.get(key);
            switch (key) {
                case "carrera":
                    sql.filter("car.id", new Long(value));
                    break;
                case "consejero":
                    sql.filter("con.id", new Long(value));
                    break;
                case "alumno":
                    Octavia subQuery = Octavia.query()
                            .from(ReunionAlumnoConsejero.class, "rac")
                            .join("agendaConsejero agco", "alumnoConsejero alco")
                            .join("alco.alumno alu")
                            .filter("alu.id",  new Long(value));
                    sql.__()
                            .exists(subQuery)
                            .linkedBy("acon.id", "agco.id");
                    break;
                default:
                    break;
            }
        }
    }

}
