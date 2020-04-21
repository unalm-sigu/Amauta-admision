package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import pe.edu.lamolina.amauta.dao.academico.OrientacionCarreraDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.OrientacionCarrera;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.enums.EstadoEnum;

@Repository
public class OrientacionCarreraDAOH extends AbstractEasyDAO<OrientacionCarrera> implements OrientacionCarreraDAO {

    public OrientacionCarreraDAOH() {
        super();
        setClazz(OrientacionCarrera.class);
    }

    @Override
    public OrientacionCarrera find(long id) {
        Octavia sql = Octavia.query()
                .from(OrientacionCarrera.class, "oc")
                .join("carrera ca")
                .filter("oc.id", id);
        return find(sql);
    }

    @Override
    public List<OrientacionCarrera> allByCarrera(Carrera carrera) {
        Octavia sql = Octavia.query()
                .from(OrientacionCarrera.class, "oc")
                .join("carrera ca")
                .filter("ca.id", carrera);

        return all(sql);
    }

    @Override
    public OrientacionCarrera findLastByCarrera(Carrera carrera) {
        Octavia sql = Octavia.query()
                .from(OrientacionCarrera.class, "oc")
                .join("carrera ca")
                .filter("ca.id", carrera)
                .orderBy("oc.id desc")
                .limit(1);

        return find(sql);
    }

    @Override
    public OrientacionCarrera findForPlanCurriculares(OrientacionCarrera orientacion) {
        Octavia sql = Octavia.query()
                .selectDistinct("ori")
                .from(PlanCurricular.class, "pc")
                .join("orientacionCarrera ori")
                .filter("ori.id", orientacion);

        return find(sql);
    }

    @Override
    public OrientacionCarrera findForAlumnos(OrientacionCarrera orientacion) {
        Octavia sql = Octavia.query()
                .selectDistinct("ori")
                .from(Alumno.class, "pc")
                .join("orientacionCarrera ori")
                .filter("ori.id", orientacion);

        return find(sql);
    }

//    @Override
//    public List<OrientacionCarrera> allByIdCarreraDynatable(DynatableFilter filter, Long idCarrera) {
//        DynatableSql sql = new DynatableSql(filter)
//                .from(OrientacionCarrera.class, "oc")
//                .join("carrera ca")
//                .filter("ca.id", idCarrera)
//                .searchFields("ca.nombre", "oc.nombre", "ca.codigo", "oc.codigo")
//                .orderBy("ca.id desc");
//
//        return all(sql);
//    }

    @Override
    public List<OrientacionCarrera> allByCarreraEstado(Carrera carrera, EstadoEnum estadoEnum) {
        Octavia sql = Octavia.query()
                .from(OrientacionCarrera.class, "oc")
                .join("carrera ca")
                .filter("ca.id", carrera)
                .filter("oc.estado", estadoEnum.name());

        return all(sql);
    }

    @Override
    public List<OrientacionCarrera> allByCarreras(List<Carrera> carreras) {
        Octavia sql = Octavia.query()
                .from(OrientacionCarrera.class, "oc")
                .join("carrera ca")
                .in("ca.id", carreras);

        return all(sql);
    }

}
