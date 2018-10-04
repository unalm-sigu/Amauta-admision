package pe.edu.lamolina.pivot.dao.posgrado.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.posgrado.TarifaCarrera;
import pe.edu.lamolina.pivot.dao.posgrado.TarifaCarreraDAO;

@Repository
public class TarifaCarreraDAOH extends AbstractEasyDAO<TarifaCarrera> implements TarifaCarreraDAO {

    public TarifaCarreraDAOH() {
        super();
        setClazz(TarifaCarrera.class);
    }

    @Override
    public List<TarifaCarrera> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(TarifaCarrera.class, "tc")
                .join("carrera c", "cicloInicio ca")
                .searchFields("c.nombre")
                .orderBy("tc.id desc");

        return all(sql);
    }

    @Override
    public List<TarifaCarrera> allByCarrera(Carrera carrera) {
        Octavia sql = new Octavia()
                .from(TarifaCarrera.class, "tc")
                .join("carrera car", "cicloInicio ca")
                .orderBy("tc.id desc");
        sql.filter("car.id", carrera);
        return all(sql);
    }

}
