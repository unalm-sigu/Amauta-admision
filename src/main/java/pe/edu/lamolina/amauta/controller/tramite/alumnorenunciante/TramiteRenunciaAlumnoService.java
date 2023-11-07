
package pe.edu.lamolina.amauta.controller.tramite.alumnorenunciante;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.TramiteRenunciaAlumno;


public interface TramiteRenunciaAlumnoService {

    public List<TramiteRenunciaAlumno> allTramitesRenuciaByFilter(DynatableFilter filter);

    public void saveAlumnoRenuncia(TramiteRenunciaAlumno tramiteRenunciaAlumno, DataSessionPivot ds);

    public List<TipoTramite> allTipoTramite();

    public void saveAlumnoRenunciaCarrera(TramiteRenunciaAlumno tramiteRenunciaAlumno, DataSessionPivot ds);

}
