package pe.edu.lamolina.amauta.controller.consejeria.aconsejadostutor;

import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.controller.consejeria.aconsejadostutor.view.ResumenEncuestaTutoria;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.bean.AconsejadoEstadoBean;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.tutoria.AlumnoCualidad;
import pe.edu.lamolina.model.tutoria.CitaConsejeroAlumno;
import pe.edu.lamolina.model.tutoria.InformeFinalTutoria;
import pe.edu.lamolina.model.tutoria.PlanTutorial;

public interface AconsejadosTutorService {

    Consejero findConsejero(Persona persona, CicloAcademico ciclo);

    InformeFinalTutoria findInforme(Consejero consejero, CicloAcademico ciclo, DataSessionPivot ds);

    List<AlumnoConsejero> allByDynatable(DynatableFilter filter, CicloAcademico ciclo, Persona persona);

    List<AlumnoConsejero> allByDynatableByCarrera(DynatableFilter filter, CicloAcademico ciclo, Persona tutor, Carrera carrera, DataSessionPivot ds);

    List<AlumnoConsejero> allByDynatableByCarreraReporte(DynatableFilter filter, CicloAcademico ciclo, Persona tutor, Carrera carrera);

    AconsejadoEstadoBean allByPersona(Persona persona, CicloAcademico ciclo);

    void matriculaAutorizacion(MatriculaResumen matriculaResumen, DataSessionPivot ds);

    Persona findPersona(Long idPersona);

    AconsejadoEstadoBean allByPersonaCarrera(Persona person, CicloAcademico ciclo, Carrera carrera, DataSessionPivot ds);

    void eliminarAlumnoConsejero(Long idAlumnoConsejero);

    void quitarTutor(Long idAlumnoConsejero);

    Map<Long, List<PlanTutorial>> allPlanes(List<Alumno> alumnos, CicloAcademico ciclo);

    Map<Long, List<AlumnoCualidad>> allCualidades(List<Alumno> alumnos, CicloAcademico ciclo);

    Map<Long, CitaConsejeroAlumno> allCitas(List<Alumno> alumnos, CicloAcademico ciclo);

    List<PreguntaExamen> allPreguntasEncuesta(CicloAcademico ciclo);

    List<ResumenEncuestaTutoria> allDataEncuesta(Consejero consejero, List<PreguntaExamen> preguntas, CicloAcademico ciclo, DataSessionPivot ds);

}
