package pe.edu.lamolina.amauta.controller.tramite.tramiteTitulo;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteTitulo;

public interface TramitesTituloService {

    public List<TramiteTitulo> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds);

    public String TituloReporte(Tramite tramite, DataSessionPivot ds);

    public void saveTitulo(TramiteTitulo tramiteBachiller, DataSessionPivot ds);

    public void anularTitulo(TramiteTitulo tramiteTitulo, DataSessionPivot ds);

    public Tramite findByTramite(Long id);

}
