package pe.edu.lamolina.pivot.dao.posgrado.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.posgrado.TarifaCarrera;
import pe.edu.lamolina.pivot.dao.posgrado.TarifaCarreraDAO;

@Repository
public class TarifaCarreraDAOH extends AbstractEasyDAO<TarifaCarrera> implements TarifaCarreraDAO {

    public TarifaCarreraDAOH() {
        super();
        setClazz(TarifaCarrera.class);
    }

    @Override
    public List<TarifaCarrera> allByDynatableCiclo(DynatableFilter filter, CicloAcademico ciclo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(TarifaCarrera.class, "tc")
                .join("carrera c", "cicloAcademico ca")
                .filter("ca.id", ciclo)
                .searchFields("c.nombre")
                .orderBy("tc.id desc");

        return all(sql);
    }

}
