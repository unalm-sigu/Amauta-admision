package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.Curso;

public interface AlumnoCicloCursoDAO extends EasyDAO<AlumnoCicloCurso> {

    List<AlumnoCicloCurso> findHistorial(Alumno alumno);

    List<AlumnoCicloCurso> allByAlumno(Alumno alumno);

    List<AlumnoCicloCurso> allByAlumnoOrdeyByCurso(Alumno alumno);

    List<AlumnoCicloCurso> allActivoByAlumno(Alumno alumno);

    List<AlumnoCicloCurso> allAprobadoActivoByAlumno(Alumno alumno);

    List<AlumnoCicloCurso> allDesaprobadoActivoByAlumno(Alumno alumno);

    Long countByCursoAlumno(Curso curso, Alumno alumno);
}
