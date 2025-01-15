package pe.edu.lamolina.amauta.controller.tramite.bachiller;

import java.util.List;
import org.springframework.ui.Model;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.tramite.TramiteBachiller;

public interface TramitesBachillerService {

    List<TramiteBachiller> allTramitesByFilter(DynatableFilter filter);

    void reporte(Long tramite, Model model, DataSessionPivot ds);

    void saveBachiller(TramiteBachiller tramiteBachiller, DataSessionPivot ds);

    void anular(TramiteBachiller tramiteBachiller, DataSessionPivot ds);

}
