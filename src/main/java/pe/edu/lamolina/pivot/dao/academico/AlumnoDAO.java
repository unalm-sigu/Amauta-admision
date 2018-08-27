package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.academico.alumno.AlumnoResumen;
import pe.edu.lamolina.pivot.controller.academico.matriculable.MatriculableResumen;

public interface AlumnoDAO extends EasyDAO<Alumno> {

    Alumno findByCodigo(String codigoAlumno);

    Alumno findFlatByCodigo(String codigoAlumno);

    Alumno findLock(Long id);

    List<Alumno> allByPersona(Persona persona);

    List<Alumno> allByPlanCurricular(PlanCurricular planCurricular);

    List<Alumno> allByRolDynatable(DynatableFilter filter, List<Carrera> carreras);

    AlumnoResumen findResumen();

    List<Alumno> allByCicloRolDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, String codigo, List<Long> filtros);

    MatriculableResumen findResumenByCiclo(CicloAcademico cicloAcademico);

    List<Alumno> allByName(String nombre);

    Alumno findByPersona(Persona persona, CicloAcademico cicloAcademico);

    List<Alumno> allIngresantePregradoByCiclo(ModalidadEstudio modalidad, CicloAcademico cicloAcademico, List<Alumno> alumnoExclude);

    List<Alumno> allByNameModalidadEstudioCiclo(String nombre, ModalidadEstudio modalidad, CicloAcademico cicloAcademico);

    List<Alumno> allByPersonas(List<Persona> personas);

    Alumno find(Alumno alumno);

    Long countByPlanCurricular(PlanCurricular plan);

    Alumno findByPersonaCicloIngreso(Persona persona, CicloAcademico ciclo);

    List<Alumno> allBySituaciones(ModalidadEstudio modalidad, List<SituacionAcademica> situaciones);

    void updateCicloActivoSituacionAcad(Alumno alumno);

    void updateSituacionAcad(Alumno alumno);

    void updateSituacionCicloCapa(Alumno alumno);

    Alumno findAllInfo(Long id);

    void updateCicloActivoRegular(Alumno alumno);

    Alumno findSituacionAcademica(Alumno alumno);

    List<Alumno> allByNombreFacultad(String name, Facultad facultad);
    
}
