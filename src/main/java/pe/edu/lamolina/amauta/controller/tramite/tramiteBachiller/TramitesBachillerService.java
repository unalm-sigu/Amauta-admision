package pe.edu.lamolina.amauta.controller.tramite.tramiteBachiller;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.tramite.TramiteBachiller;

public interface TramitesBachillerService {

    public List<TramiteBachiller> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds);

}
