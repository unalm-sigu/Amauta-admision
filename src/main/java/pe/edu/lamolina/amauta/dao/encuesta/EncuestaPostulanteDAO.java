package pe.edu.lamolina.amauta.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.examen.OpcionPregunta;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.EncuestaPostulante;

public interface EncuestaPostulanteDAO extends EasyDAO<EncuestaPostulante> {

    List<EncuestaPostulante> allByPreguntasCiclo(List<PreguntaExamen> preguntas, CicloPostula ciclo);

    List<EncuestaPostulante> allByPreguntaOpcionCiclo(PreguntaExamen pregunta, OpcionPregunta opcion, CicloPostula ciclo);

    void unificarFrases(OpcionPregunta opcion, String permanece, String modifica, CicloPostula ciclo);

}
