package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.model.academico.CursoCachimbos;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.pivot.dao.academico.CursoCachimbosDAO;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;

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
                .join("curso cur", "carrera car", "car.facultad fac", "cicloAcademico ciclo")
                .filter("ciclo.id", cicloAcademico)
                .searchFields("cur.nombre", "car.nombre")
                .orderBy("cuca.id desc");
        sql.beginRelativeFilters();
        return sql.all(getCurrentSession());
    }

    @Override
    public CursoCachimbos findByCursoCiclo(CursoCachimbos cursoCachimbos) {
        Octavia sql = Octavia.query()
                .from(CursoCachimbos.class, "cc")
                .join("curso cur", "carrera car", "car.facultad fac", "cicloAcademico ciclo")
                .filter("cur.id", cursoCachimbos.getCurso())
                .filter("ciclo.id", cursoCachimbos.getCicloAcademico());
        return (CursoCachimbos) sql.find(getCurrentSession());
    }
}
