package pe.edu.lamolina.pivot.controller.academico.alumno;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaCurso;

public interface AlumnoService {

    List<Alumno> allAlumnosByCicloDynatable(DynatableFilter filter, String codigo, List<Long> filtros);

    AlumnoResumen findResumen();

    List<MatriculaCurso> allMatriculaCursoByAlumno(Long idAlumno);

    Alumno findAlumno(Alumno alumno, CicloAcademico academico);

    List<AlumnoCicloCurso> findAlumnoHistorial(Alumno alumno);

    List<AlumnoCiclo> allPromediosByAlumno(Alumno alumno);
    
    List<AlumnoCicloCurso> allPromediosByAlumnoOrderByCurso(Alumno alumno);

}
