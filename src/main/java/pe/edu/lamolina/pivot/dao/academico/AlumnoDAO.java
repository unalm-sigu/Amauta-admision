package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.general.Persona;

public interface AlumnoDAO extends Crud<Alumno> {

    Alumno findByCodigo(String codigoAlumno);

    Alumno findLock(Long id);

    List<Alumno> allByPersona(Persona persona);

}
