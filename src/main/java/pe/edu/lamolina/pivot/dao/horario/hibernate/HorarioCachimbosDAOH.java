package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.List;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioCachimbosDAO;
import pe.edu.lamolina.pivot.model.horario.HorarioCachimbos;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;

@Repository
public class HorarioCachimbosDAOH extends AbstractEasyDAO<HorarioCachimbos> implements HorarioCachimbosDAO {

    public HorarioCachimbosDAOH() {
        super();
        setClazz(HorarioCachimbos.class);
    }

    @Override
    public List<HorarioCachimbos> allHorarioCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(HorarioCachimbos.class, "hoca")
                .join("carrera car", "car.facultad fac", "cicloAcademico ciclo")
                .filter("ciclo.id", cicloAcademico)
                .searchFields("car.nombre")
                .orderBy("hoca.id desc");
        sql.beginRelativeFilters();
        return sql.all(getCurrentSession());
    }
}
