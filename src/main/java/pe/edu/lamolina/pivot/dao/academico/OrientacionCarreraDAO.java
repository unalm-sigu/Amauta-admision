package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.OrientacionCarrera;
import pe.edu.lamolina.model.enums.EstadoEnum;

public interface OrientacionCarreraDAO extends EasyDAO<OrientacionCarrera> {

    List<OrientacionCarrera> allByCarrera(Carrera carrera);

    OrientacionCarrera findLastByCarrera(Carrera carrera);

    List<OrientacionCarrera> allByIdCarreraDynatable(DynatableFilter filter, Long idCarrera);

    OrientacionCarrera find(Long id);

    List<OrientacionCarrera> allByCarreraEstado(Carrera carrera, EstadoEnum estadoEnum);

    List<OrientacionCarrera> allByCarreras(List<Carrera> carreras);

}
