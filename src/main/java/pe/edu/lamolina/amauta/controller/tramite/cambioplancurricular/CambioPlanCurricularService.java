package pe.edu.lamolina.amauta.controller.tramite.cambioplancurricular;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import org.springframework.ui.Model;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.CambioPlanCurricular;

public interface CambioPlanCurricularService {

    public List<CambioPlanCurricular> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds);

    public List<CicloAcademico> getCiclosVeinte();

    public void save(CambioPlanCurricular cambioPlanCurricular, DataSessionPivot ds);

    public void anular(Long cambioPlanCurricular, DataSessionPivot ds);

    public List<Alumno> searchAlumno(String nombre, DataSessionPivot ds);

    public void reporte(Model model, Long idCambioPlanCurricular, DataSessionPivot ds);

    public ObjectNode searchPlanCurricular(Long idAlumno, DataSessionPivot ds);

}
