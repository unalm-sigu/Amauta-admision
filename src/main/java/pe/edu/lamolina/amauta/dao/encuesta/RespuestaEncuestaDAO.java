package pe.edu.lamolina.amauta.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.RespuestaEncuesta;

public interface RespuestaEncuestaDAO extends EasyDAO<RespuestaEncuesta> {

    List<RespuestaEncuesta> allByAlumnosEncuestaCiclo(List<Alumno> alumnos, ExamenVirtual encuesta, CicloAcademico ciclo);

}
