package pe.edu.lamolina.pivot.controller.tramiteTrabajo;

import java.util.List;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.TramiteSubvencion;

public interface TramiteTrabajoService {

    public List<TramiteSubvencion> allTramiteSubvByColabo(Persona persona, CicloAcademico cicloAcademico);

    public void updateTramiteSubvencion(TramiteSubvencion tramiteSubvencion, Usuario usuario);

}
