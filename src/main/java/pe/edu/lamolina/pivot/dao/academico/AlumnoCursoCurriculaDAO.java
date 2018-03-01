package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;

public interface AlumnoCursoCurriculaDAO extends EasyDAO<AlumnoCursoCurricula> {

    List<AlumnoCursoCurricula> allNoOpcionalByAlumno(Alumno alumno);

   List<AlumnoCursoCurricula> allByAlumno(Alumno alumno, Long numeroCiclo);

    List<AlumnoCursoCurricula> allCiclosAlumno(Alumno alumno);

}
