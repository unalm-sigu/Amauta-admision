package pe.edu.lamolina.amauta.controller.tramite.reincorporacion;

import java.util.List;
import org.thymeleaf.context.Context;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.Reincorporacion;

public interface TramiteReincorporacionService {

    public List<Reincorporacion> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds);

    public void saveReincorporacion(Reincorporacion retiro, DataSessionPivot ds);

    public Context reporte(Long idTramite, DataSessionPivot ds);

    public List<CicloAcademico> getCiclos(DataSessionPivot ds);

    public List<CicloAcademico> getCiclosVeinte(DataSessionPivot ds);

}
