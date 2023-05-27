package pe.edu.lamolina.amauta.controller.consejeria.aconsejadostutor;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.bean.AconsejadoEstadoBean;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Carrera;

public interface AconsejadosTutorService {

    List<AlumnoConsejero> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, Persona persona);

    List<AlumnoConsejero> allByDynatableByCarrera(DynatableFilter filter, CicloAcademico cicloAcademico, Persona tutor, Carrera carrera, DataSessionPivot ds);

    List<AlumnoConsejero> allByDynatableByCarreraReporte(DynatableFilter filter, CicloAcademico cicloAcademico, Persona tutor, Carrera carrera);

    AconsejadoEstadoBean allByPersona(Persona persona, CicloAcademico cicloAcademico);

    void matriculaAutorizacion(MatriculaResumen matriculaResumen, DataSessionPivot ds);

    Persona findPersona(Long idPersona);

    AconsejadoEstadoBean allByPersonaCarrera(Persona person, CicloAcademico cicloAcademico, Carrera carrera, DataSessionPivot ds);

    void eliminarAlumnoConsejero(Long idAlumnoConsejero);

    void quitarTutor(Long idAlumnoConsejero);
}
