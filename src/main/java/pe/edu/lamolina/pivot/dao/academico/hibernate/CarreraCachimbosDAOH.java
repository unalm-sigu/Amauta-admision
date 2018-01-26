package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraCachimbosDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CarreraCachimbos;
import pe.edu.lamolina.model.academico.CicloAcademico;

@Repository
public class CarreraCachimbosDAOH extends AbstractEasyDAO<CarreraCachimbos> implements CarreraCachimbosDAO {

    public CarreraCachimbosDAOH() {
        super();
        setClazz(CarreraCachimbos.class);
    }

    @Override
    public List<CarreraCachimbos> allCarreraCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CarreraCachimbos.class, "cc")
                .join("carrera car", "car.facultad fac", "cicloAcademico ciclo")
                .filter("ciclo.id", cicloAcademico)
                .searchFields("car.nombre")
                .orderBy("car.codigo");
        sql.beginRelativeFilters();
        return sql.all(getCurrentSession());
    }

    @Override
    public List<CarreraCachimbos> allByCicloAcademico(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(CarreraCachimbos.class, "cc")
                .join("carrera car", "car.facultad fac", "cicloAcademico ciclo")
                .filter("ciclo.id", cicloAcademico);
        return sql.all(getCurrentSession());
    }

    @Override
    public CarreraCachimbos findByCarreraCiclo(Carrera carrera, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(CarreraCachimbos.class, "cc")
                .join("carrera car", "car.facultad fac", "cicloAcademico ciclo")
                .filter("car.id", carrera)
                .filter("ciclo.id", cicloAcademico);
        return (CarreraCachimbos) sql.find(getCurrentSession());
    }

    @Override
    public void allRegenerateByCiclo(CicloAcademico cicloAcademico) {

        StringBuilder sql = new StringBuilder();
        sql.append("  update ").append(CarreraCachimbos.class.getName()).append(" cc ");
        sql.append("  set cc.sinHorario = cc.ingresantes      ");
        sql.append("  ,  cc.conHorario = 0      ");
        sql.append("  ,  cc.suspendidos = 0      ");
        sql.append("  ,  cc.matriculados = 0      ");
        sql.append("  where cc.cicloAcademico.id = :CICLO ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CICLO", cicloAcademico.getId());
        query.executeUpdate();
    }

}
