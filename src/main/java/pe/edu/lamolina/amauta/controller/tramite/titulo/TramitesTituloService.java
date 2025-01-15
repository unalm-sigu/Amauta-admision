package pe.edu.lamolina.amauta.controller.tramite.titulo;

import java.util.List;
import org.springframework.ui.Model;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.tramite.TramiteTitulo;

public interface TramitesTituloService {

    List<TramiteTitulo> allTramitesByFilter(DynatableFilter filter);

    void reporte(Long tramite, Model model, DataSessionPivot ds);

    void saveTitulo(TramiteTitulo tramiteBachiller, DataSessionPivot ds);

    void anularTitulo(TramiteTitulo tramiteTitulo, DataSessionPivot ds);

}
