package pe.edu.lamolina.amauta.controller.tramite.titulo;

import java.util.List;
import org.thymeleaf.context.Context;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.tramite.TramiteTitulo;

public interface TramitesTituloService {

    public List<TramiteTitulo> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds);

    public Context reporte(Long tramite, DataSessionPivot ds);

    public void saveTitulo(TramiteTitulo tramiteBachiller, DataSessionPivot ds);

    public void anularTitulo(TramiteTitulo tramiteTitulo, DataSessionPivot ds);

}
