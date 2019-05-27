package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.OrientacionCarrera;
import pe.edu.lamolina.model.enums.EstadoEnum;

public interface OrientacionCarreraDAO extends EasyDAO<OrientacionCarrera> {

    List<OrientacionCarrera> allByCarrera(Carrera carrera);

    OrientacionCarrera findLastByCarrera(Carrera carrera);

    OrientacionCarrera findForPlanCurriculares(OrientacionCarrera orientacion);

    OrientacionCarrera findForAlumnos(OrientacionCarrera orientacion);

//    List<OrientacionCarrera> allByIdCarreraDynatable(DynatableFilter filter, Long idCarrera);
    List<OrientacionCarrera> allByCarreraEstado(Carrera carrera, EstadoEnum estadoEnum);

    List<OrientacionCarrera> allByCarreras(List<Carrera> carreras);

}
