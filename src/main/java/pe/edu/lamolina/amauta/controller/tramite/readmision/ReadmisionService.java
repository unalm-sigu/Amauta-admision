package pe.edu.lamolina.amauta.controller.tramite.readmision;

import java.util.List;
import org.springframework.ui.Model;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.Readmision;

public interface ReadmisionService {

    public List<Readmision> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds);

    public List<CicloAcademico> getCiclosSeis();

    public void save(Readmision readmision, DataSessionPivot ds);

    public void anular(Long readmision, DataSessionPivot ds);

    public List<Alumno> searchAlumno(String nombre, DataSessionPivot ds);

    public void reporte(Model model, Long idReadmision, DataSessionPivot ds);

}
