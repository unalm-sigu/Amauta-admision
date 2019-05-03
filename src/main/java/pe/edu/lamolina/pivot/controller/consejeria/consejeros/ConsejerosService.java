package pe.edu.lamolina.pivot.controller.consejeria.consejeros;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ConsejerosService {

    void saveConsejeroByDocente(Docente docente, DataSessionPivot ds);

    void updateEstado(Consejero consejero, DataSessionPivot ds);

    List<Consejero> allByCarreraDynatable(Carrera carrera, DynatableFilter filter);

    Consejero finByIdPersona(Persona persona);

    List<Docente> allDocenteByNombreFacultad(String nombre, Facultad facultad);

    ConsejeriaEstado findConsejeroByStateAndCarrera(Long carrera);

    void asignarAlumnosAleatorio(Long carrera, DataSessionPivot ds);

    void desasignarAlumnos(Long carrera, DataSessionPivot ds);

    AConsejeroEstado findAConsejadosByStateCarrera(Long carrera, DataSessionPivot ds);

    Colaborador findColaboradorByIdPersona(Long idPersona);

    Carrera findbByNombre(Long idcarrera);

    List<Carrera> allCarreraByPersonaCiclo(Persona persona, CicloAcademico ciclo);

    List<Consejero> allByCarrera(String nombre, Carrera carrera);
    
    List<Alumno> allAlumnosByConsejero(Consejero consejero);
}
