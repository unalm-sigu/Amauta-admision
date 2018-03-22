package pe.edu.lamolina.pivot.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.TemaExamenVirtual;

public interface TemaExamenVirtualDAO extends EasyDAO<TemaExamenVirtual> {

    public List<TemaExamenVirtual> allByEvaluacion(ExamenVirtual evaluacionVirtual);

    public TemaExamenVirtual findByEvaluacionOrden(TemaExamenVirtual temaEvaluacionVirtual, Integer orden);

    public List<TemaExamenVirtual> allActivoByEvaluacion(ExamenVirtual evaluacionVirtual);

    public List<TemaExamenVirtual> allInactivoByEvaluacion(ExamenVirtual evaluacionVirtual);

    public TemaExamenVirtual findLastInactivo(ExamenVirtual evaluacionVirtual);

    public TemaExamenVirtual findLastActivo(ExamenVirtual evaluacionVirtual);

    public TemaExamenVirtual findTemaExamenVirtual(TemaExamenVirtual temaExamenVirtual);

}
