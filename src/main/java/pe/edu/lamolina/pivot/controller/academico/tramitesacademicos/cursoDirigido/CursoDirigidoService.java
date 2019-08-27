package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.cursoDirigido;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.tramite.CursoDirigido;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface CursoDirigidoService {

    public List<CursoDirigido> allByFacultades(DynatableFilter filters,CicloAcademico ciclo);

    public void update(CursoDirigido cursoDirigido, DataSessionPivot ds);

    void anular(CursoDirigido cursoDirigido, DataSessionPivot ds);

}
