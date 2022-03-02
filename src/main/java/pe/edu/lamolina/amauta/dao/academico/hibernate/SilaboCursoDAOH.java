package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.SilaboCurso;
import pe.edu.lamolina.amauta.dao.academico.SilaboCursoDAO;

@Repository
public class SilaboCursoDAOH extends AbstractEasyDAO<SilaboCurso> implements SilaboCursoDAO {

    public SilaboCursoDAOH() {
        super();
        setClazz(SilaboCurso.class);
    }

    @Override
    public List<SilaboCurso> allByDynatable(DynatableFilter filter) {

        DynatableSql sql = new DynatableSql(filter)
                .from(SilaboCurso.class, "sc")
                .join("curso cu", "cu.departamentoAcademico da", "da.facultad fa")
                .join("sc.departamentoAcademico depa", "cu.modalidadEstudio mo")
                .searchFields("cu.nombre", "cu.codigo", "fa.nombre", "fa.nombre", "da.nombre", "mo.nombre")
                .orderBy("sc.id");
        sql.beginRelativeFilters();
        setDepartamento(filter, sql);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<SilaboCurso> allByIds(ArrayList<Long> silabus) {
        Octavia sql = new Octavia()
                .from(SilaboCurso.class, "sc")
                .join("curso cu", "cu.departamentoAcademico da", "da.facultad fa")
                .join("sc.departamentoAcademico depa", "cu.modalidadEstudio mo")
                .in("sc.id", silabus);
        return this.all(sql);
    }

    private void setDepartamento(DynatableFilter filter, DynatableSql sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }

        for (String key : queries.keySet()) {
            if (!key.equals("departamento")) {
                continue;
            }
            String values = (String) queries.get(key);
            sql.filter("depa.id", new Long(values));
        }
    }

    @Override
    public List<SilaboCurso> all() {
        Octavia sql = new Octavia()
                .from(SilaboCurso.class, "sc")
                .join("curso cu", "cu.departamentoAcademico da", "da.facultad fa")
                .join("sc.departamentoAcademico depa", "cu.modalidadEstudio mo")
                .orderBy("cu.id","depa.id");
        return this.all(sql);
    }

}
