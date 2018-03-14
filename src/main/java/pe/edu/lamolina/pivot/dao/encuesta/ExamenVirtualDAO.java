package pe.edu.lamolina.pivot.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

public interface ExamenVirtualDAO extends EasyDAO<ExamenVirtual> {

    List<ExamenVirtual> allEncuestasByDynatable(DynatableFilter filter);

    ExamenVirtual findEncuestaActiva();

    ExamenVirtual findEncuestaActivaByCiclo(CicloPostula ciclo);

    ExamenVirtual findEncuestaUltimoCodigo();

    Long countRespuestas(ExamenVirtual encuesta);

    Long countRespuestasByCiclo(ExamenVirtual encuesta, CicloPostula ciclo);

}
