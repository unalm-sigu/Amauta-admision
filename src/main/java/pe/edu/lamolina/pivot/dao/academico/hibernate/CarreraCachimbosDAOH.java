package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraCachimbosDAO;
import pe.edu.lamolina.pivot.model.academico.CarreraCachimbos;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;

@Repository
public class CarreraCachimbosDAOH extends AbstractEasyDAO<CarreraCachimbos> implements CarreraCachimbosDAO {

    public CarreraCachimbosDAOH() {
        super();
        setClazz(CarreraCachimbos.class);
    }

    @Override
    public List<CarreraCachimbos> allCarreraCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CarreraCachimbos.class, "caca")
                .join( "carrera car", "car.facultad fac", "cicloAcademico ciclo")
                .filter("ciclo.id", cicloAcademico)
                .searchFields("car.nombre")
                .orderBy("caca.id desc");
        sql.beginRelativeFilters();
        return sql.all(getCurrentSession());
    }
}
