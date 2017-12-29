package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.ResumenAlumnoEvaluacion;

public interface ResumenAlumnoEvaluacionDAO extends EasyDAO<ResumenAlumnoEvaluacion> {

    List<ResumenAlumnoEvaluacion> allByAlumnoGrupoSeccion(Alumno alumno, GrupoSeccion gpoSeccion);

    List<ResumenAlumnoEvaluacion> allByGrupoSeccion(GrupoSeccion grupoSeccion);

}
