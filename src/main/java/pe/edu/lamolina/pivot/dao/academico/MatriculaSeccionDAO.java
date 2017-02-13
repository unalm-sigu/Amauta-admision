package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.MatriculaSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;

public interface MatriculaSeccionDAO extends Crud<MatriculaSeccion> {

    List<MatriculaSeccion> allBySeccion(Seccion seccion);

    MatriculaSeccion find(Long id);

    MatriculaSeccion findByAlumnoSeccion(Alumno alumno, Seccion seccion);

}
