package pe.edu.lamolina.amauta.controller.tramite.retirocicloexcepcional;

import java.util.List;
import org.springframework.ui.Model;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.RetiroCiclo;

public interface TramiteRetiroExcepcionalService {

    public List<RetiroCiclo> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds);

    public void saveRetiro(RetiroCiclo retiro, DataSessionPivot ds);

    public void anular(RetiroCiclo retiroCiclo, DataSessionPivot ds);

    public void reporte(Long idTramite, DataSessionPivot ds, Model model);

    public List<CicloAcademico> getCiclosVeinte(DataSessionPivot ds);

}
