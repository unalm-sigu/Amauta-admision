package pe.edu.lamolina.amauta.controller.academico.promedio;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.Reincorporacion;

public interface ReincorporadosService {

    List<Reincorporacion> allReincorporacionesByCicloActivo(List<Alumno> alumnos, List<CicloAcademico> ciclosActivos);

    List<Reincorporacion> allReincorporacionesByAlumno(Alumno alumno, CicloAcademico cicloActivo);
}
