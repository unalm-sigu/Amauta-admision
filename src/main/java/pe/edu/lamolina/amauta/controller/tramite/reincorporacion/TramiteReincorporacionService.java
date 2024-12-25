package pe.edu.lamolina.amauta.controller.tramite.reincorporacion;

import java.util.List;
import org.springframework.ui.Model;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.Reincorporacion;

public interface TramiteReincorporacionService {

    List<Reincorporacion> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds);

    void saveReincorporacion(Reincorporacion retiro, DataSessionPivot ds);

    void reporte(Long idTramite, Model model, DataSessionPivot ds);

    List<CicloAcademico> getCiclos(DataSessionPivot ds);

}
