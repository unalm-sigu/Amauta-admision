package pe.edu.lamolina.amauta.controller.tramite.bachiller;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteBachiller;

public interface TramitesBachillerService {

    public List<TramiteBachiller> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds);

    public String bachillerReporte(Tramite tramite, DataSessionPivot ds);

    public void saveBachiller(TramiteBachiller tramiteBachiller, DataSessionPivot ds);

    public void anular(TramiteBachiller tramiteBachiller, DataSessionPivot ds);

    public Tramite findByTramite(Long id);

}
