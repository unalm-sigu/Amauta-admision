package pe.edu.lamolina.amauta.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.examen.BloquePreguntas;
import pe.edu.lamolina.model.examen.SubTituloExamen;

public interface BloquePreguntasDAO extends EasyDAO<BloquePreguntas> {

    List<BloquePreguntas> allBysubtitulos(List<SubTituloExamen> subTitulos);

    List<BloquePreguntas> allBySubtitulo(SubTituloExamen subtitulo);

    BloquePreguntas findBloqueEvaluacionVirtual(Long instancia);

}
