package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoAdicionalCurriculaDAO;
import pe.edu.lamolina.pivot.model.academico.CursoAdicionalCurricula;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.dynatable.DynatableFilter;

@Repository
public class CursoAdicionalCurriculaDAOH extends AbstractDAO<CursoAdicionalCurricula> implements CursoAdicionalCurriculaDAO {

    public CursoAdicionalCurriculaDAOH() {
        super();
        setClazz(CursoAdicionalCurricula.class);
    }

    @Override
    public List<CursoAdicionalCurricula> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CursoAdicionalCurricula.class, "cc")
                .join("curso cur", "planCurricular pc")
                .searchFields("cur.nombre")
                .filter("pc.id", filter.getQueries().get("planc"))
                .orderBy("cur.nombre desc");
        return sql.all(getCurrentSession());
    }
}
