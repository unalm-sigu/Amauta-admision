package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.OrientacionCarreraDAO;
import pe.edu.lamolina.pivot.model.academico.OrientacionCarrera;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

@Repository
public class OrientacionCarreraDAOH extends AbstractDAO<OrientacionCarrera> implements OrientacionCarreraDAO {

    public OrientacionCarreraDAOH() {
        super();
        setClazz(OrientacionCarrera.class);
    }

    @Override
    public List<OrientacionCarrera> allByCarrera(Carrera carrera) {
        Octavia sql = Octavia.query()
                .from(OrientacionCarrera.class, "oc")
                .join("carrera ca")
                .filter("ca.id", carrera);
        return sql.all(getCurrentSession());
    }

    @Override
    public OrientacionCarrera findLastByCarrera(Carrera carrera) {
        Octavia sql = Octavia.query()
                .from(OrientacionCarrera.class, "oc")
                .join("carrera ca")
                .filter("ca.id", carrera)
                .orderBy("oc.id desc")
                .limit(1);
        return (OrientacionCarrera) sql.find(getCurrentSession());
    }

    @Override
    public List<OrientacionCarrera> allByIdCarreraDynatable(DynatableFilter filter, Long idCarrera) {
        DynatableSql sql = new DynatableSql(filter)
                .from(OrientacionCarrera.class, "oc")
                .join("carrera ca")
                .filter("ca.id", idCarrera)
                .searchFields("ca.nombre", "oc.nombre", "ca.codigo", "oc.codigo")
                .orderBy("ca.id desc");
        return sql.all(getCurrentSession());
    }

    @Override
    public OrientacionCarrera find(Long id) {
        Octavia sql = Octavia.query()
                .from(OrientacionCarrera.class, "oc")
                .join("carrera ca")
                .filter("oc.id", id);
        return (OrientacionCarrera) sql.find(getCurrentSession());
    }

    @Override
    public List<OrientacionCarrera> allByCarreraEstado(Carrera carrera, EstadoEnum estadoEnum) {
        Octavia sql = Octavia.query()
                .from(OrientacionCarrera.class, "oc")
                .join("carrera ca")
                .filter("ca.id", carrera)
                .filter("oc.estado", estadoEnum.name());
        return sql.all(getCurrentSession());
    }

    @Override
    public List<OrientacionCarrera> allByCarreras(List<Carrera> carreras) {
        Octavia sql = Octavia.query()
                .from(OrientacionCarrera.class, "oc")
                .join("carrera ca")
                .in("ca.id", carreras);
        return sql.all(getCurrentSession());
    }

}
