package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.academico.alumno.AlumnoResumen;
import pe.edu.lamolina.pivot.controller.academico.matriculable.MatriculableResumen;

public interface AlumnoDAO extends EasyDAO<Alumno> {

    Alumno findByCodigo(String codigoAlumno);

    Alumno findLock(Long id);

    List<Alumno> allByPersona(Persona persona);

    List<Alumno> allByRolDynatable(DynatableFilter filter, String codigo, List<Long> filtros);

    AlumnoResumen findResumen();

    List<Alumno> allByCicloRolDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, String codigo, List<Long> filtros);

    MatriculableResumen findResumenByCiclo(CicloAcademico cicloAcademico);

    List<Alumno> allAlumnoByName(String nombre);

    Alumno findByPersona(Persona persona, CicloAcademico cicloAcademico);

    List<Alumno> allIngresantePregradoByCiclo(ModalidadEstudio modalidad, CicloAcademico cicloAcademico, List<Alumno> alumnoExclude);

    List<Alumno> allAlumnoIngresantePregradoByNameCiclo(String nombre, ModalidadEstudio modalidad, CicloAcademico cicloAcademico);

    List<Alumno> allByPersonas(List<Persona> personas);

    public Alumno findAlumno(Alumno alumno);

    public Alumno findByPersonaCicloIngreso(Persona personaDB, CicloAcademico ciclo);

}
