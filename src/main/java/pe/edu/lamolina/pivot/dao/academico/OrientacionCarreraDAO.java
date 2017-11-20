package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.OrientacionCarrera;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

public interface OrientacionCarreraDAO extends Crud<OrientacionCarrera> {

    List<OrientacionCarrera> allByCarrera(Carrera carrera);

    OrientacionCarrera findLastByCarrera(Carrera carrera);

    List<OrientacionCarrera> allByIdCarreraDynatable(DynatableFilter filter, Long idCarrera);

    OrientacionCarrera find(Long id);

    List<OrientacionCarrera> allByFilter(Carrera carrera, EstadoEnum estadoEnum);

}
