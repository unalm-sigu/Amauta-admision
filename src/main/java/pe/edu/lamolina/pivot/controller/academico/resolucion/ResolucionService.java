package pe.edu.lamolina.pivot.controller.academico.resolucion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.TipoResolucion;

public interface ResolucionService {

    List<Resolucion> allTramitesByFilter(DynatableFilter filter);

    List<TipoResolucion> allTiposResolucion();

}
