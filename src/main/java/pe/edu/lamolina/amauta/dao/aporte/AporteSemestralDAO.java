package pe.edu.lamolina.amauta.dao.aporte;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.aporte.Aporte;
import pe.edu.lamolina.model.aporte.AporteSemestral;

public interface AporteSemestralDAO extends EasyDAO<AporteSemestral> {

    AporteSemestral findActivoByAlumno(Alumno alumno, Aporte aporte);

    List<AporteSemestral> allActivosByAlumno(Alumno alumno);

    List<AporteSemestral> allByAporteAlumnos(Aporte aporte, List<Alumno> alumnos);
    
    AporteSemestral findSemestralByAlumno(Alumno alumno);
}
