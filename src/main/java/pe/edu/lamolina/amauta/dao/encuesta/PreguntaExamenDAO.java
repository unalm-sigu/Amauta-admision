package pe.edu.lamolina.amauta.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.OpcionPregunta;
import pe.edu.lamolina.model.examen.PreguntaExamen;

public interface PreguntaExamenDAO extends EasyDAO<PreguntaExamen> {

    List<PreguntaExamen> allForEncuestaByDynatable(DynatableFilter filter, ExamenVirtual encuesta);

    List<PreguntaExamen> allForExamenByDynatable(DynatableFilter filter, ExamenVirtual examen);

    PreguntaExamen findMayorNumero(ExamenVirtual evaluacionVirtual);

    List<PreguntaExamen> allReferencia(PreguntaExamen pregunta);

    PreguntaExamen findPregunta(Long pregunta);

    List<PreguntaExamen> allActivasByEncuesta(ExamenVirtual encuesta);

    List<PreguntaExamen> allByEncuesta(ExamenVirtual encuesta);

    List<PreguntaExamen> allByEncuestas(List<ExamenVirtual> encuestas);

    List<PreguntaExamen> allMayoresByNumero(Integer numero, ExamenVirtual encuesta);

    List<PreguntaExamen> allByOpcionesReferencia(List<OpcionPregunta> opcionesReferencia);

    List<PreguntaExamen> allWithOtrosByEncuesta(ExamenVirtual encuesta);

}
