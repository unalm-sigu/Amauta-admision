package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.ResumenAlumnoEvaluacion;

public interface ResumenAlumnoEvaluacionDAO extends Crud<ResumenAlumnoEvaluacion> {

    List<ResumenAlumnoEvaluacion> allByAlumnoGrupoSeccion(Alumno alumno, GrupoSeccion gpoSeccion);

    List<ResumenAlumnoEvaluacion> allByGrupoSeccion(GrupoSeccion grupoSeccion);

}
