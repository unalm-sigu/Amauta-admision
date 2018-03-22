package pe.edu.lamolina.pivot.controller.academico.encuesta.pregunta;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.OpcionPregunta;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.examen.TemaExamenVirtual;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface PreguntaEncuestaService {

    List<PreguntaExamen> allPreguntaEvaluacionVirtual(DynatableFilter filter, ExamenVirtual encuesta);

    void savePregunta(PreguntaExamen pregunta, DataSessionPivot ds);

    void updatePregunta(PreguntaExamen pregunta, DataSessionPivot ds);

    PreguntaExamen findPregunta(Long pregunta);

    List<PreguntaExamen> allReferencia(PreguntaExamen pregunta);

    void deletePregunta(PreguntaExamen pregunta);

    void cambiarEstadoPregunta(PreguntaExamen pregunta, DataSessionPivot ds);

    List<OpcionPregunta> allOpcionesByName(String nombre, ExamenVirtual encuesta);

    List<PreguntaExamen> allPreguntasActivasByEncuesta(ExamenVirtual encuesta);

    PreguntaExamen findPreguntaMaxOrden(List<PreguntaExamen> preguntas);

    PreguntaExamen findPreguntaNumeroTop(Long idEncuesta);

    ExamenVirtual findEncuesta(Long idEncuesta);

    void upateNumeroPregunta(PreguntaExamen pregunta);

    List<TemaExamenVirtual> allTemaExamenVirtualByExamenVirtual(ExamenVirtual encuesta);

}
