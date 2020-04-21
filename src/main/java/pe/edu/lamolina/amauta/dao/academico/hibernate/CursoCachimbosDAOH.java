package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import java.util.Map;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCachimbos;
import pe.edu.lamolina.model.horario.SeccionCursoCachimbos;
import pe.edu.lamolina.amauta.dao.academico.CursoCachimbosDAO;

@Repository
public class CursoCachimbosDAOH extends AbstractEasyDAO<CursoCachimbos> implements CursoCachimbosDAO {

    public CursoCachimbosDAOH() {
        super();
        setClazz(CursoCachimbos.class);
    }

    @Override
    public List<CursoCachimbos> allByDynatableCiclo(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CursoCachimbos.class, "cuca")
                .join("curso cur", "carrera car", "car.facultad fac", "cur.departamentoAcademico dep", "cicloAcademico ciclo")
                .filter("ciclo.id", cicloAcademico)
                .searchFields("cur.nombre", "cur.codigo", "car.nombre", "dep.nombreLargo", "dep.nombre", "fac.nombre")
                .orderBy("cuca.id desc")
                .orderBy("car.codigo asc");
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

    //@Override
//    public List<CursoCachimbos> allCursoCachimbos(CicloAcademico cicloAcademico) {
//        Octavia sql = Octavia.query()
//                .from(CursoCachimbos.class, "cc")
//                .join("curso cur", "carrera car", "car.facultad fac", "cicloAcademico ciclo")
//                .filter("ciclo.id", cicloAcademico);
//        return sql.all(getCurrentSession());
//    }
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
    public List<CursoCachimbos> allByCursosCicloCarrera(List<Curso> cursos, CicloAcademico cicloAcademico, Carrera carrera) {
        Octavia sql = Octavia.query()
                .from(CursoCachimbos.class, "cc")
                .join("curso cur", "carrera car", "car.facultad fac", "cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .filter("car.id", carrera)
                .in("cur.id", cursos)
                .orderBy("car.id");
        return sql.all(getCurrentSession());
    }

    @Override
    public List<CursoCachimbos> allByCicloFromSeccionCursoCachimbo(CicloAcademico ciclo) {

        Octavia sql = Octavia.query()
                .selectDistinct("cc")
                .from(SeccionCursoCachimbos.class, "scc")
                .join("cursoCachimbos cc", "seccion sec", "cc.curso cur", "cc.carrera car", "car.facultad fac", "cc.cicloAcademico ca")
                .filter("ca.id", ciclo);

        return all(sql);

    }

    @Override
    public List<CursoCachimbos> allByCursosCiclo(List<Curso> cursos, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(CursoCachimbos.class, "cc")
                .join("curso cur", "carrera car", "car.facultad fac", "cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .in("cur.id", cursos);

        return sql.all(getCurrentSession());
    }

}
