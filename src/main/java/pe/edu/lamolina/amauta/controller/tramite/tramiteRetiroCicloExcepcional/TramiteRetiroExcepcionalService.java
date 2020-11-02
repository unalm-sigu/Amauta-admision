package pe.edu.lamolina.amauta.controller.tramite.tramiteRetiroCicloExcepcional;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.model.tramite.Tramite;

public interface TramiteRetiroExcepcionalService {

    public List<RetiroCiclo> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds);

    public void saveRetiro(RetiroCiclo retiro, DataSessionPivot ds);

    public String reporte(Tramite tramite, DataSessionPivot ds);

}
