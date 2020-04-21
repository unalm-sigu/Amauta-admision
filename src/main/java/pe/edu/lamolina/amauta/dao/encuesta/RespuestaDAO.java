package pe.edu.lamolina.amauta.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.calificacion.Respuesta;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

public interface RespuestaDAO extends EasyDAO<Respuesta> {

    List<Respuesta> allByPostulante(List<Respuesta> respuestas);

    List<Respuesta> allRespuestaByCiclo(CicloPostula ciclo);

    void deleteByCiclo(CicloPostula ciclo);

}
