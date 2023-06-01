package pe.edu.lamolina.amauta.controller.consejeria.plantutoria;

import java.util.List;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.tutoria.AlumnoCualidad;
import pe.edu.lamolina.model.tutoria.PlanTutorial;
import pe.edu.lamolina.model.tutoria.TipoCualidadAlumno;

public interface PlanTutoriaService {

    Alumno findAlumno(Alumno alumno);

    boolean verificarConsejero(Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds);

    boolean tienePermiso(Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds);

    Consejero findConsejero(Persona persona, Carrera carrera);

    AlumnoConsejero findAlumnoConsejero(Alumno alumno, CicloAcademico ciclo);

    List<TipoCualidadAlumno> allTiposCualidades();

    List<AlumnoCualidad> allCualidadesAlumno(Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds);

    List<PlanTutorial> allPlanesTutoria(Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds);

    void saveCaracteristicas(List<AlumnoCualidad> cualidades, Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds);

    void savePlanTutorial(List<PlanTutorial> planes, Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds);

    void deletePlanTutorial(PlanTutorial plan, Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds);

    boolean tieneCaracteristicas(Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds);

    boolean tieneMapaEmpatia(Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds);

    boolean tienePlanTutorial(Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds);

}
