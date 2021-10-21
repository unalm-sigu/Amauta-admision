package pe.edu.lamolina.amauta.dao.contabilidad;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.contabilidad.ItemJustificacionGasto;
import pe.edu.lamolina.model.contabilidad.JustificacionGasto;

public interface ItemJustificacionGastoDAO extends EasyDAO<ItemJustificacionGasto> {

    List<ItemJustificacionGasto> allByJustificacion(JustificacionGasto justificacion);

}
