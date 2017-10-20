package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.zelpers.dao.SqlUtil;

@Repository
public class CarreraDAOH extends AbstractDAO<Carrera> implements CarreraDAO {

    public CarreraDAOH() {
        super();
        setClazz(Carrera.class);
    }

    @Override
    public Carrera findByCodigo(String codigo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ca")
                .filter("ca.codigo", codigo);
        return this.find(sqlUtil);
    }

    @Override
    public List<Carrera> allByModalidadEstudio(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Carrera.class, "ca")
                .join("modalidadEstudio me", "facultad fa")
                .searchFields("ca.nombre", "ca.codigo")
                .orderBy("ca.id desc");
        return sql.all(getCurrentSession());
    }
}
