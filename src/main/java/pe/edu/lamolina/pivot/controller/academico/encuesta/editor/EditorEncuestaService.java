package pe.edu.lamolina.pivot.controller.academico.encuesta.editor;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface EditorEncuestaService {

    CicloPostula findCicloActivo();

    List<ExamenVirtual> allEncuesta(DynatableFilter filter);

    ExamenVirtual findEncuesta(Long idEncuesta);

    void saveEncuesta(ExamenVirtual encuesta, DataSessionPivot ds);

    void updateEncuesta(ExamenVirtual encuesta);

    void delete(ExamenVirtual encuesta);

    void cambiarEstadoEncuesta(ExamenVirtual encuesta, DataSessionPivot ds);

    void duplicar(ExamenVirtual encuesta, DataSessionPivot ds);

    List<PreguntaExamen> allPreguntasByEncuesta(ExamenVirtual encuesta);

}
