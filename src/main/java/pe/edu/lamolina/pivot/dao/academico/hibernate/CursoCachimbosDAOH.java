package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import java.util.Map;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.model.academico.CursoCachimbos;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.pivot.dao.academico.CursoCachimbosDAO;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;

@Repository
public class CursoCachimbosDAOH extends AbstractEasyDAO<CursoCachimbos> implements CursoCachimbosDAO {

    public CursoCachimbosDAOH() {
        super();
        setClazz(CursoCachimbos.class);
    }

    @Override
    public List<CursoCachimbos> allCursoCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CursoCachimbos.class, "cuca")
                .join("curso cur", "carrera car", "car.facultad fac", "cur.departamentoAcademico dep", "cicloAcademico ciclo")
                .filter("ciclo.id", cicloAcademico)
                .searchFields("cur.nombre", "car.nombre")
                .orderBy("cuca.id desc");
        sql.beginRelativeFilters();
        this.setCarrera(filter, sql);
        return sql.all(getCurrentSession());
    }

    private void setCarrera(DynatableFilter filter, DynatableSql sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }
        if (queries.get("car.id") == null) {
            return;
        }
        sql.filter("car.id", queries.get("car.id"));
    }

    @Override
    public CursoCachimbos findByCursoCiclo(CursoCachimbos cursoCachimbos) {
        Octavia sql = Octavia.query()
                .from(CursoCachimbos.class, "cc")
                .join("curso cur", "carrera car", "car.facultad fac", "cicloAcademico ciclo")
                .filter("cur.id", cursoCachimbos.getCurso())
                .filter("car.id", cursoCachimbos.getCarrera())
                .filter("ciclo.id", cursoCachimbos.getCicloAcademico());
        return (CursoCachimbos) sql.find(getCurrentSession());
    }

    @Override
    public List<CursoCachimbos> allCursoCachimbos(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(CursoCachimbos.class, "cc")
                .join("curso cur", "carrera car", "car.facultad fac", "cicloAcademico ciclo")
                .filter("ciclo.id", cicloAcademico);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<CursoCachimbos> allByCarreraCiclo(CicloAcademico cicloAcademico, Carrera carrera) {
        Octavia sql = Octavia.query()
                .from(CursoCachimbos.class, "cc")
                .join("curso cur", "carrera car", "car.facultad fac", "cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .filter("car.id", carrera)
                .orderBy("car.id");

        return sql.all(getCurrentSession());
    }

    @Override
    public List<CursoCachimbos> allByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(CursoCachimbos.class, "cc")
                .join("curso cur", "carrera car", "car.facultad fac", "cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .orderBy("car.id");

        return sql.all(getCurrentSession());
    }

    @Override
    public List<CursoCachimbos> allByCursoCiclo(List<Curso> cursos, CicloAcademico cicloAcademico, Carrera carrera) {
        Octavia sql = Octavia.query()
                .from(CursoCachimbos.class, "cc")
                .join("curso cur", "carrera car", "car.facultad fac", "cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .filter("car.id", carrera)
                .in("cur.id", cursos)
                .orderBy("car.id");
        return sql.all(getCurrentSession());
    }

}
