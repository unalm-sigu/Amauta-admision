package pe.edu.lamolina.amauta.controller.tramite.bachiller;

import java.util.List;
import org.thymeleaf.context.Context;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.tramite.TramiteBachiller;

public interface TramitesBachillerService {

    public List<TramiteBachiller> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds);

    public Context reporte(Long tramite, DataSessionPivot ds);

    public void saveBachiller(TramiteBachiller tramiteBachiller, DataSessionPivot ds);

    public void anular(TramiteBachiller tramiteBachiller, DataSessionPivot ds);


}
