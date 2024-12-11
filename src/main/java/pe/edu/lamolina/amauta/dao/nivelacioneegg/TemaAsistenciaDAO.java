package pe.edu.lamolina.amauta.dao.nivelacioneegg;

import java.util.Date;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.TemaAsistencia;

public interface TemaAsistenciaDAO extends EasyDAO<TemaAsistencia> {

    List<TemaAsistencia> allSeccionByDynatable(DynatableFilter filter, CursoNivelacion cursoNivelacion);

    List<TemaAsistencia> allByCursoNivelacion(CursoNivelacion cursoNivelacion);

    List<TemaAsistencia> allByCursosNivelaciones(List<CursoNivelacion> cursosNivelaciones);

    TemaAsistencia findByCursoNivelacionFecha(CursoNivelacion cursoNivelacion, Date fecha);

}
