package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.pivot.dao.academico.RecorridoIngresanteDAO;

@Repository
public class RecorridoIngresanteDAOH extends AbstractEasyDAO<RecorridoIngresante> implements RecorridoIngresanteDAO {

    public RecorridoIngresanteDAOH() {
        super();
        setClazz(RecorridoIngresante.class);
    }

    @Override
    public List<RecorridoIngresante> allByDynatableCiclo(DynatableFilter filter, CicloAcademico ciclo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(RecorridoIngresante.class, "ri")
                .join("cicloAcademico ci", "alumno al")
                .join("al.persona per", "al.carrera car")
                .leftJoin("turnoEntrevistaObuae tu", "per.tipoDocumento td")
                .filter("ci.id", ciclo)
                .searchFields("al.codigo", "car.nombre", "per.numeroDocIdentidad", "td.simbolo")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("ri.id desc");

        return all(sql);
    }

}
