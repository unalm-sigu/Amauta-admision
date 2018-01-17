package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioCachimbosDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.horario.HorarioCachimbos;

@Repository
public class HorarioCachimbosDAOH extends AbstractEasyDAO<HorarioCachimbos> implements HorarioCachimbosDAO {

    public HorarioCachimbosDAOH() {
        super();
        setClazz(HorarioCachimbos.class);
    }

    @Override
    public List<HorarioCachimbos> allByDynatable(DynatableFilter filter, CicloAcademico ciclo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(HorarioCachimbos.class, "hoca")
                .join("carrera car", "car.facultad fac", "cicloAcademico ciclo")
                .filter("ciclo.id", ciclo)
                .searchFields("car.nombre", "hoca.codigo")
                .orderBy("hoca.id desc");
        sql.beginRelativeFilters();

        return all(sql);
    }

    @Override
    public List<HorarioCachimbos> allByCicloCarrera(CicloAcademico ciclo, Carrera carrera) {
        Octavia sql = Octavia.query()
                .from(HorarioCachimbos.class, "hoca")
                .join("carrera car", "car.facultad fac", "cicloAcademico ciclo")
                .filter("ciclo.id", ciclo)
                .filter("car.id", carrera)
                .orderBy("hoca.id desc");

        return all(sql);
    }

    @Override
    public List<HorarioCachimbos> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(HorarioCachimbos.class, "hoca")
                .join("carrera car", "car.facultad fac", "cicloAcademico ciclo")
                .filter("ciclo.id", ciclo)
                .orderBy("hoca.id desc");

        return all(sql);
    }

    @Override
    public HorarioCachimbos findMaxCodeOrderByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(HorarioCachimbos.class, "hoca")
                .join("carrera car", "car.facultad fac", "cicloAcademico ciclo")
                .filter("ciclo.id", ciclo)
                .orderBy("CONVERT( SUBSTRING(hoca.codigo, 3) ,  UNSIGNED )   desc")
                .limit(1);

        return find(sql);
    }

    @Override
    public HorarioCachimbos find(HorarioCachimbos horarioCachimbos) {
        Octavia sql = Octavia.query()
                .from(HorarioCachimbos.class, "hoca")
                .join("carrera car", "car.facultad fac", "cicloAcademico ciclo")
                .filter("hoca.id", horarioCachimbos);

        return find(sql);
    }

}
