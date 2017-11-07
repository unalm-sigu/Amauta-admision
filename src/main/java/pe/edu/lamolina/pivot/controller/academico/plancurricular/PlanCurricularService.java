package pe.edu.lamolina.pivot.controller.academico.plancurricular;

import java.util.List;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.academico.OrientacionCarrera;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

public interface PlanCurricularService {

    List<Carrera> allCarrerasByFilter(Facultad facultad, EstadoEnum estadoEnum);

    List<OrientacionCarrera> allOrientacionCarreraByFilter(Carrera carrera, EstadoEnum estadoEnum);

}
