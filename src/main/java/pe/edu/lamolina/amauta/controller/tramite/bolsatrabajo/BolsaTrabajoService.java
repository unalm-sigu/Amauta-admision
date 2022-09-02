package pe.edu.lamolina.amauta.controller.tramite.bolsatrabajo;

import java.util.List;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.tramite.TramiteSubvencion;

public interface BolsaTrabajoService {

    List<TramiteSubvencion> allSubvencionesBySupervisor(Persona persona, CicloAcademico ciclo);

    void updateTramiteSubvencion(TramiteSubvencion tramiteSubvencion, DataSessionPivot ds);

}
