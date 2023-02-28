package pe.edu.lamolina.amauta.controller.tramite.retirocicloexcepcional;

import java.util.List;
import org.springframework.ui.Model;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.RetiroCiclo;

public interface TramiteRetiroExcepcionalService {

    List<RetiroCiclo> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds);

    String saveRetiro(RetiroCiclo retiro, DataSessionPivot ds);

    void anular(RetiroCiclo retiroCiclo, DataSessionPivot ds);

    void reporte(Long idTramite, DataSessionPivot ds, Model model);

    List<CicloAcademico> getCiclosVeinte(DataSessionPivot ds);

}
