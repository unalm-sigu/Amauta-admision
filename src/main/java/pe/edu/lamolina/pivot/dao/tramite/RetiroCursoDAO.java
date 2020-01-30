package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.tramite.RetiroCurso;

public interface RetiroCursoDAO extends EasyDAO<RetiroCurso> {

    List<RetiroCurso> allByAlumno(Alumno alumno);

    List<RetiroCurso> allInfo();

    List<RetiroCurso> allRetiroCursoByAlumno(Alumno alumno);

}
