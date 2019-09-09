package pe.edu.lamolina.pivot.controller.consejeria.consejeros;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.ConsejeriaResumen;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ConsejerosService {

    void saveConsejeroByDocente(Docente docente, CicloAcademico ciclo, DataSessionPivot ds);

    void updateEstado(Consejero consejero, CicloAcademico ciclo, DataSessionPivot ds);

    List<Consejero> allByCarreraDynatable(Carrera carrera, CicloAcademico cicloAcademico, DynatableFilter filter);

    Consejero finByIdPersona(Persona persona);

    List<Docente> allDocenteByNombreFacultad(String nombre, Facultad facultad);

    void asignarAlumnosAleatorio(Carrera carrera, CicloAcademico ciclo, DataSessionPivot ds);

    void desasignarAlumnos(Carrera carrera, CicloAcademico ciclo, DataSessionPivot ds);

    Carrera findbByNombre(Long idcarrera);

    List<Carrera> allCarreraByPersonaCiclo(Persona persona, CicloAcademico ciclo);

    List<Consejero> allByCarrera(String nombre, Carrera carrera);

    List<Alumno> allAlumnosByConsejero(Consejero consejero);

    void revisarConsejeria(Carrera carrera, CicloAcademico cicloAcademico, boolean forzar, DataSessionPivot ds);

    ConsejeriaResumen getResumenByCarreraCiclo(Carrera carrera, CicloAcademico cicloAcademico);

    List<Alumno> allAlumnosByConsejero(List<Consejero> consejero);

    List<Alumno> allAlumnoByName(String nombre, CicloAcademico cicloAcademico);

    void saveAlumnosConsejero(Consejero consejero, DataSessionPivot ds);

    List<AlumnoConsejero> allAlumnosConsejeros(List<Consejero> consejeros, CicloAcademico cicloAcademico, EstadoEnum... estados);

    List<MatriculaResumen> allMatriculadosByCicloAndCarrera(CicloAcademico cicloAcademico, List<Carrera> carreras);

    List<AlumnoConsejero> allAlumnosOtraEspecialidad(Carrera carreraConsejero, CicloAcademico ciclo);

}
