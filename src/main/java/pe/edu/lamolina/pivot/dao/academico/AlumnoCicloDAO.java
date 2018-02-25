package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;

public interface AlumnoCicloDAO extends EasyDAO<AlumnoCiclo> {

    AlumnoCiclo findByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico);

    List<AlumnoCiclo> allByAlumno(Alumno alumno);

}
