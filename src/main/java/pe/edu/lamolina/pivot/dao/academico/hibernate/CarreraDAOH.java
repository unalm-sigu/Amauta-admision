package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.model.general.Compania;

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
    public List<Carrera> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Carrera.class, "ca")
                .join("modalidadEstudio me", "facultad fa")
                .searchFields("ca.nombre", "ca.codigo")
                .orderBy("ca.id desc");
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Carrera> allByCompania(Compania compania) {
        SqlUtil sqlUtil = new SqlUtil("ca")
                .parents("modalidadEstudio mo", "_mo.compania co")
                .filter("co.id", compania);
        return all(sqlUtil);
    }

    @Override
    public Carrera find(Long id) {
        Octavia sql = Octavia.query()
                .from(Carrera.class, "ca")
                .join("modalidadEstudio me", "facultad fa")
                .filter("ca.id", id);
        return (Carrera) sql.find(getCurrentSession());
    }

    @Override
    public List<Carrera> allByNombre(String nombre) {
        Octavia sql = Octavia.query()
                .from(Carrera.class, "ca")
                .join("modalidadEstudio me", "facultad fa")
                .filter("ca.nombre", "like", nombre);
        return sql.all(getCurrentSession());
    }
}
