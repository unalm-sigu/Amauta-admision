package pe.edu.lamolina.pivot.controller.academico.resolucion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.tramite.Resolucion;

public interface ResolucionService {

    List<Resolucion> allTramitesByFilter(DynatableFilter filter);

}
