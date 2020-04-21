package pe.edu.lamolina.amauta.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.OpcionPregunta;
import pe.edu.lamolina.model.examen.PreguntaExamen;

public interface OpcionPreguntaDAO extends EasyDAO<OpcionPregunta> {

    void deleteByPregunta(PreguntaExamen preguntaEvaluacionVirtual);

    List<OpcionPregunta> allByName(String nombre, ExamenVirtual evaluacion);

    OpcionPregunta findByPreguntaReferencia(PreguntaExamen preguntaEvaluacionVirtual);

    List<OpcionPregunta> allByPreguntas(List<PreguntaExamen> preguntas);

    List<OpcionPregunta> allByPregunta(PreguntaExamen pregunta);

    List<OpcionPregunta> allOtrosByPregunta(PreguntaExamen pregunta);

}
