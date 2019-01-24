package pe.edu.lamolina.pivot.controller.consejeria.consejeria;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ConsejeroService {

    List<Docente> allDocenteByNombreAndCarrera(String nombre, String facultadid);

    List<Docente> allDocenteByCarrera(String nombre);

    List<Docente> allDocente();

    public Docente findById(Long idDocente);

    public Carrera findCarreraByIdFacultad(Long idFaculta);

    public Colaborador findColaboradorByIdPersona(Long idPersona);
    
    public Colaborador findColaboradorDocenteByIdPersona(Long idPersona, Long IdCargo);

    public void saveConsejero(Consejero consejero, DataSessionPivot ds);

    public List<Carrera> allByCarreraByNombre(String nombre, List<Carrera> carreras);

    public Carrera findbByNombre(Long idcarrera);

    public List<Consejero> allConsejerosbyDynatableCarrera(DynatableFilter filter);
    
    public List<DepartamentoAcademico> allDeptByIdFacultad(String facultadid);

    public List<Docente> allDocenteByNombreAndCarreraAndDeparts(String nombre, List<DepartamentoAcademico> departs);

    public Consejero find(Long idConsejero);

    public Consejero findByIdColaborador(Long id);

    public List<Carrera> allCarreraByIdDocente(long idDocente);

}
