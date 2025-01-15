package pe.edu.lamolina.amauta.controller.academico.tramitesacademicos.cursoDirigido;

import java.util.List;
import org.springframework.ui.Model;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.CursoDirigido;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Facultad;

public interface CursoDirigidoService {

    List<CursoDirigido> allByFacultades(DynatableFilter filters, CicloAcademico ciclo);

    void update(CursoDirigido cursoDirigido, DataSessionPivot ds);

    void anular(CursoDirigido cursoDirigido, DataSessionPivot ds);

    void alllistCursoDirigidoFac(Facultad facultad, Model model, DataSessionPivot ds);

}
