package pe.edu.lamolina.pivot.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.examen.TemaExamenVirtual;
import pe.edu.lamolina.model.examen.SubTituloExamen;

public interface SubTituloExamenDAO extends EasyDAO<SubTituloExamen> {

    List<SubTituloExamen> allByTemas(List<TemaExamenVirtual> temas);

    SubTituloExamen findByTemaOrden(SubTituloExamen subTituloEvaluacionVirtual, Integer orden);

    List<SubTituloExamen> allByTema(TemaExamenVirtual tema);

    SubTituloExamen findSubTituloEvaluacionVirtual(Long instancia);

    SubTituloExamen findLastActivo(TemaExamenVirtual temaEvaluacionVirtual);

    SubTituloExamen findLastInactivo(TemaExamenVirtual temaEvaluacionVirtual);

    List<SubTituloExamen> allActivoByTema(TemaExamenVirtual temaEvaluacionVirtual);

}
