package pe.edu.lamolina.amauta.controller.academico.tramitesacademicos.historialAlumno;

import java.util.List;
import javax.servlet.http.HttpSession;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;


public interface HistorialAlumnoService {
    
    DynatableResponse listAlumnos(DynatableFilter filter, HttpSession httpSession);
    
    List<TipoDocIdentidad> allDocumentos();
    
    List<ModalidadEstudio> allModalidadEstudioByCodes(List<ModalidadEstudioEnum> codes, Compania compania);
    
    List<Facultad> allFacultad(String nombre, Compania compania);
    
    List<Carrera> allCarrera(String nombre, Compania compania);

    boolean registrarAlumno(PersonaDto personDto, HttpSession httpSession);      

    Persona update(Alumno alumno, DataSessionPivot ds);
    
    void save(Alumno alumno, DataSessionPivot ds);

    Persona findPersonaByDocIdentidad(Persona persona);

    Persona findPersona(Persona persona);

}
