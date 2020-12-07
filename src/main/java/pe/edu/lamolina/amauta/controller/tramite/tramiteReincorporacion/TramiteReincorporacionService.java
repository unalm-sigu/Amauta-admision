package pe.edu.lamolina.amauta.controller.tramite.tramiteReincorporacion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.Tramite;

public interface TramiteReincorporacionService {

    public List<Reincorporacion> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds);

    public void saveReincorporacion(Reincorporacion retiro, DataSessionPivot ds);

    public String reporte(Tramite tramite, DataSessionPivot ds);

    public List<CicloAcademico> getCiclos(DataSessionPivot ds);

}
