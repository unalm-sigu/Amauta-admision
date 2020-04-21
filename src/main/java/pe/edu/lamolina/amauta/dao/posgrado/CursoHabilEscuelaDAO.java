package pe.edu.lamolina.amauta.dao.posgrado;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.posgrado.CursoHabilEscuela;

public interface CursoHabilEscuelaDAO extends EasyDAO<CursoHabilEscuela> {

    List<CursoHabilEscuela> allAlumnos(List<Alumno> alumnos);

    List<CursoHabilEscuela> allByAlumnoAndCiclo(Alumno alumno, CicloAcademico cicloAcademico);
}
