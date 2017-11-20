package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoOpcionalCurriculaDAO;
import pe.edu.lamolina.pivot.model.academico.CursoOpcionalCurricula;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;

@Repository
public class CursoOpcionalCurriculaDAOH extends AbstractDAO<CursoOpcionalCurricula> implements CursoOpcionalCurriculaDAO {

    public CursoOpcionalCurriculaDAOH() {
        super();
        setClazz(CursoOpcionalCurricula.class);
    }

    @Override
    public List<CursoOpcionalCurricula> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CursoOpcionalCurricula.class, "cc")
                .join("curso cur", "planCurricular pc")
                .searchFields("cur.nombre")
                .filter("pc.id", filter.getQueries().get("planc"))
                .orderBy("cur.nombre desc");
        return sql.all(getCurrentSession());
    }
}
