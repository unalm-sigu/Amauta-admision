package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.tema;

import java.util.List;
import pe.edu.lamolina.model.examen.BloquePreguntas;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.SubTituloExamen;
import pe.edu.lamolina.model.examen.TemaExamenVirtual;

public interface TemaEncuestaService {

    public List<TemaExamenVirtual> allTema(ExamenVirtual examenVirtual);

    public void saveTema(TemaExamenVirtual temaEvaluacionVirtual);

    public void updateTema(TemaExamenVirtual temaEvaluacionVirtual);

    public void saveSubTitulo(SubTituloExamen subTituloEvaluacionVirtual);

    public void updateSubTitulo(SubTituloExamen subTituloEvaluacionVirtual);

    public void updateBloque(BloquePreguntas bloqueEvaluacionVirtual);

    public void saveBloque(BloquePreguntas bloqueEvaluacionVirtual);

    public void deleteSubTitulo(SubTituloExamen subTituloEvaluacionVirtual);

    public void deleteTema(TemaExamenVirtual temaEvaluacionVirtual);

    public void deleteBloque(BloquePreguntas bloqueEvaluacionVirtual);

    public TemaExamenVirtual findTema(TemaExamenVirtual temaEvaluacionVirtual);

    public BloquePreguntas findBloque(BloquePreguntas bloqueEvaluacionVirtual);

    public SubTituloExamen findSubTitulo(SubTituloExamen subTituloEvaluacionVirtual);

    public void itemSort(Integer itemSort, Long instancia, String tipo);

    public void estado(Long instancia, String tipo);

    public ExamenVirtual findEncuesta(Long idEncuesta);

}
