package pe.edu.lamolina.amauta.dao.academico;

import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.TipoEvaluacion;

import java.util.List;

public interface TipoEvaluacionDAO extends EasyDAO<TipoEvaluacion> {
    List<TipoEvaluacion> allByDynaTable(DynatableFilter filter);

    List<TipoEvaluacion> findByOrdenGreater(int orden);

    boolean existsByCodigo(String codigo);

    boolean existsByNombre(String nombre);
}
