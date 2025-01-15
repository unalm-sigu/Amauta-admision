package pe.edu.lamolina.amauta.dao.nivelacioneegg;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.ExamenCursoNivelacion;

public interface ExamenCursoNivelacionDAO extends EasyDAO<ExamenCursoNivelacion> {

    List<ExamenCursoNivelacion> allByCursoNivelacion(CursoNivelacion cursoNiv);

    List<ExamenCursoNivelacion> allByCursosNivelaciones(List<CursoNivelacion> cursosNiv);

}
