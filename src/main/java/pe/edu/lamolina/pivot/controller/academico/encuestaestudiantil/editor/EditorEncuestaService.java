package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.editor;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.encuestaestudiantil.ConfiguraEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.CursoSinEncuesta;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.examen.TipoExamenVirtual;
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

    List<TipoExamenVirtual> allTipoEncuesta();

    List<Curso> allCursoByName(String nombre);

    void addCursoSinEncuesta(CursoSinEncuesta cursoSinEncuesta, DataSessionPivot ds);

    List<Curso> allCursoSinEncuesta(ExamenVirtual encuesta, DataSessionPivot ds);

    void removeCursoSinEncuesta(CursoSinEncuesta cursoSinEncuesta, DataSessionPivot ds);

    ConfiguraEncuesta getConfiguracion(ExamenVirtual encuesta, DataSessionPivot ds);

    ObjectNode toJson(ConfiguraEncuesta configuraEncuesta);

    void saveConfigEncuesta(ConfiguraEncuesta configuraEncuesta, DataSessionPivot ds);

    void updateConfigEncuesta(ConfiguraEncuesta configuraEncuesta, DataSessionPivot ds);

}
