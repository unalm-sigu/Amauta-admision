package pe.edu.lamolina.pivot.controller.academico.cargaacademica;

import java.util.List;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.model.academico.Seccion;

public interface CargaAcademicaService {

    List<Seccion> allByCargaAcademica(DynatableFilter filter);

    PlanCalificacion findPlanCalificacion(Long idPlanCalificacion);

}
