package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.pivot.model.academico.CursoCurricula;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.pivot.model.academico.TipoCursoCurricula;

@Repository
public class CursoCurriculaDAOH extends AbstractDAO<CursoCurricula> implements CursoCurriculaDAO {

    public CursoCurriculaDAOH() {
        super();
        setClazz(CursoCurricula.class);
    }

    @Override
    public List<CursoCurricula> allByFilter(TipoCursoCurricula tipoCursoCurricula) {
        Octavia sql = Octavia.query()
                .from(CursoCurricula.class, "cc")
                .join("tipoCursoCurricula tcc")
                .filter("ca.id", tipoCursoCurricula.getId());
        return sql.all(getCurrentSession());
    }

    @Override
    public List<CursoCurricula> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CursoCurricula.class, "cc")
                .join("tipoCursoCurricula tcc", "curso cur", "planCurricular pc")
                .searchFields("tcc.nombrecurtcc.nombre")
                .filter("pc.id", filter.getQueries().get("pc.id"))
                .filter("cc.numeroCiclo", filter.getQueries().get("cc.numeroCiclo"))
                .orderBy("cur.nombre desc");
        return sql.all(getCurrentSession());
    }

    @Override
    public List<CursoCurricula> allByNombreFilter(Long planCurriculaId, Integer numeroCiclo, String nombre, Integer limit) {
        Octavia sql = Octavia.query()
                .from(CursoCurricula.class, "cc")
                .join("tipoCursoCurricula tcc", "curso cur", "planCurricular pc", "cur.departamentoAcademico da", "da.facultad fac")
                .like("cur.nombre", nombre)
                .filter("pc.id", planCurriculaId)
                .filter("cc.numeroCiclo", "<", numeroCiclo)
                .orderBy("cur.nombre")
                .limit(limit);
        return sql.all(getCurrentSession());
    }

}
