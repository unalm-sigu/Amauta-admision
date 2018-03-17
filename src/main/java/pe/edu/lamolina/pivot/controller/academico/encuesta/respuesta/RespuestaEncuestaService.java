package pe.edu.lamolina.pivot.controller.academico.encuesta.respuesta;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.OpcionPregunta;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

public interface RespuestaEncuestaService {

    ExamenVirtual findEncuestaActivaByCiclo(CicloPostula ciclo);

    PreguntaExamen findPregunta(Long idPregunta);

    List<RespuestaItem> allResumenRespuestasOtro(DynatableFilter filter, CicloPostula ciclo);

    List<PreguntaExamen> allPreguntasOtros(ExamenVirtual encuesta);

    List<OpcionPregunta> allOpcionesOtrosByPregunta(PreguntaExamen pregunta);

    void unirFrases(OpcionPregunta opcion, CicloPostula ciclo);

    void modificarFrase(OpcionPregunta opcion, CicloPostula ciclo);

}
