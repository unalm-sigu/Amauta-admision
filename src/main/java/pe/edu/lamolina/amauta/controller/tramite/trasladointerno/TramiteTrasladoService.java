package pe.edu.lamolina.amauta.controller.tramite.trasladointerno;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteTraslado;

public interface TramiteTrasladoService {

    public List<TramiteTraslado> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds);

    public void saveTramiteTraslado(TramiteTraslado traslado, DataSessionPivot ds);

    public String reporte(Tramite tramite, DataSessionPivot ds);

    public List<Carrera> getCarreras(DataSessionPivot ds);

}
