package pe.edu.lamolina.amauta.dao.consejeria;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tutoria.PlanTutorial;

public interface PlanTutorialDAO extends EasyDAO<PlanTutorial> {

    List<PlanTutorial> allByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    List<PlanTutorial> allByAlumnosCiclo(List<Alumno> alumnos, CicloAcademico ciclo);

}
