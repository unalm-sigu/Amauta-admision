package pe.edu.lamolina.amauta.controller.academico.encuestaestudiantil.tema;

import java.util.List;
import pe.edu.lamolina.model.examen.BloquePreguntas;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.SubTituloExamen;
import pe.edu.lamolina.model.examen.TemaExamenVirtual;

public interface TemaEncuestaService {

    public List<TemaExamenVirtual> allTema(ExamenVirtual examenVirtual);

    public void saveTema(TemaExamenVirtual temaExamenVirtual);

    public void updateTema(TemaExamenVirtual temaEvaluacionVirtual);

    public void saveSubTitulo(SubTituloExamen subTituloExamen);

    public void updateSubTitulo(SubTituloExamen subTituloExamen);

    public void updateBloque(BloquePreguntas bloquePreguntas);

    public void saveBloque(BloquePreguntas bloquePreguntas);

    public void deleteSubTitulo(SubTituloExamen subTituloExamen);

    public void deleteTema(TemaExamenVirtual temaExamenVirtual);

    public void deleteBloque(BloquePreguntas bloquePreguntas);

    public TemaExamenVirtual findTema(TemaExamenVirtual temaExamenVirtual);

    public BloquePreguntas findBloque(BloquePreguntas bloquePreguntas);

    public SubTituloExamen findSubTitulo(SubTituloExamen subTituloExamen);

    public void itemSort(Integer itemSort, Long instancia, String tipo);

    public void estado(Long instancia, String tipo);

    public ExamenVirtual findEncuesta(Long idEncuesta);

}
