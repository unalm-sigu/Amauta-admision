package pe.edu.lamolina.amauta.controller.tramite.suspendidodisciplina;

import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.model.tramite.SancionDisciplina;

import java.util.List;

public interface TramiteSancionDisciplinaService {
    List<CicloAcademico> getCiclos(DataSessionPivot ds);
    List<SancionDisciplina> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds);
    String saveSancionByCiclos( SancionDTO sancionDisciplina, DataSessionPivot ds, List<CicloAcademico> idsCiclos);
}
