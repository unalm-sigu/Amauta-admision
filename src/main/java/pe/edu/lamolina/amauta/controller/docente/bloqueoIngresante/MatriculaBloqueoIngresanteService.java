package pe.edu.lamolina.amauta.controller.docente.bloqueoIngresante;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.MatriculaBloqueoIngresante;

public interface MatriculaBloqueoIngresanteService {

    List<MatriculaBloqueoIngresante> allByDynatable(DynatableFilter filter, DataSessionPivot ds);

    void copiaIngresantesAdmision(DataSessionPivot ds);

    void actualizarMatricula(Long id, DataSessionPivot ds);

    List<MatriculaBloqueoIngresante> allByCicloAcademico(Long cicloAcademico);

}
