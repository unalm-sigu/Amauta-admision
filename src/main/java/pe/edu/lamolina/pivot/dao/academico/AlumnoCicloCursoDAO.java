package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;

public interface AlumnoCicloCursoDAO extends EasyDAO<AlumnoCicloCurso> {

    AlumnoCicloCurso findByAlumnoCicloCurso(Alumno alumno, CicloAcademico cicloAcademico, Curso curso);

    List<AlumnoCicloCurso> findHistorial(Alumno alumno);

    List<AlumnoCicloCurso> allByAlumno(Alumno alumno);

    List<AlumnoCicloCurso> allOperativesByAlumno(Alumno alumno);

    List<AlumnoCicloCurso> allByAlumnoOrdeyByCurso(Alumno alumno);

    List<AlumnoCicloCurso> allOperativesByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico);
}
