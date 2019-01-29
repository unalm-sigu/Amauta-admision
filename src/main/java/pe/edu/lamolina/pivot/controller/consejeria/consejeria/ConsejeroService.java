package pe.edu.lamolina.pivot.controller.consejeria.consejeria;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ConsejeroService {

    List<Docente> allDocenteByNombreAndCarrera(String nombre, String facultadid);

    List<Docente> allDocenteByCarrera(String nombre);

    List<Docente> allDocente();

    Docente findById(Long idDocente);

    Carrera findCarreraByIdFacultad(Long idFaculta);

    Colaborador findColaboradorByIdPersona(Long idPersona);

    Colaborador findColaboradorDocenteByIdPersona(Long idPersona, Long IdCargo);

    void saveConsejeroByDocente(Docente docente, DataSessionPivot ds);

    void updateEstado(Consejero consejero, DataSessionPivot ds);

    List<Carrera> allByCarreraByNombre(String nombre, List<Carrera> carreras);

    Carrera findbByNombre(Long idcarrera);

    List<Consejero> allConsejerosbyDynatableCarrera(DynatableFilter filter);

    List<DepartamentoAcademico> allDeptByIdFacultad(String facultadid);

    List<Docente> allDocenteByNombreAndCarreraAndDeparts(String nombre, List<DepartamentoAcademico> departs);

    Consejero find(Long idConsejero);

    Consejero finByIdPersona(Persona persona);

    List<Carrera> allCarreraByIdDocente(long idDocente);

    List<Carrera> allCarreraByPersonaCiclo(Persona persona, CicloAcademico ciclo);

    List<Docente> allDocenteByNombreFacultad(String nombre, Facultad facultad);

    ConsejeroEstado findConsejeroByStateAndCarrera(Long carrera);

    void asignarAlumnosAleatorio(Long carrera, DataSessionPivot ds);

    void desasignarAlumnos(Long carrera, DataSessionPivot ds);

}
