package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.AlumnoEvaluacion;

public interface AlumnoEvaluacionDAO extends Crud<AlumnoEvaluacion> {

    List<AlumnoEvaluacion> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion);

    List<AlumnoEvaluacion> allBySeccion(Long idSeccion);

}
