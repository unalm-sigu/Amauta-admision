package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.OrientacionCarrera;

public interface OrientacionCarreraDAO extends Crud<OrientacionCarrera> {

    List<OrientacionCarrera> allByCarrera(Carrera carrera);

    OrientacionCarrera findLastByCarrera(Carrera carrera);

    public List<OrientacionCarrera> allByIdCarreraDynatable(DynatableFilter filter, Long idCarrera);

}

