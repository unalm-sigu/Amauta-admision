package pe.edu.lamolina.amauta.dao.contabilidad;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.contabilidad.ItemJustificacionGasto;
import pe.edu.lamolina.model.contabilidad.JustificacionGasto;
import pe.edu.lamolina.model.contabilidad.JustificacionGastoAlumno;

public interface JustificacionGastoAlumnoDAO extends EasyDAO<JustificacionGastoAlumno> {

    List<JustificacionGastoAlumno> allByItemJustificacion(ItemJustificacionGasto itemJustificacion);

    List<JustificacionGastoAlumno> allActivosByItemJustificacion(ItemJustificacionGasto itemJustificacion);

    List<JustificacionGastoAlumno> allByJustificacion(JustificacionGasto justificacion);

}
