package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.controller.academico.alumno.AlumnoResumen;
import pe.edu.lamolina.pivot.controller.academico.matriculable.MatriculableResumen;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.general.Persona;

public interface AlumnoDAO extends Crud<Alumno> {

    Alumno findByCodigo(String codigoAlumno);

    Alumno findLock(Long id);

    List<Alumno> allByPersona(Persona persona);

    List<Alumno> allByRolDynatable(DynatableFilter filter, String codigo, List<Long> filtros);

    AlumnoResumen findResumen();

    List<Alumno> allByCicloRolDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, String codigo, List<Long> filtros);

    MatriculableResumen findResumenByCiclo(CicloAcademico cicloAcademico);

    List<Alumno> allAlumnoByName(String nombre);

    Alumno findByPersona(Persona persona, CicloAcademico cicloAcademico);

}
