package pe.edu.lamolina.amauta.dao.nivelacioneegg;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.nivelacioneegg.AsistenciaNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.TemaAsistencia;

public interface AsistenciaNivelacionDAO extends EasyDAO<AsistenciaNivelacion> {

    List<AsistenciaNivelacion> allLeccionByDynatable(DynatableFilter filter, TemaAsistencia leccion);

    List<AsistenciaNivelacion> allByLeccion(TemaAsistencia leccion);

}
