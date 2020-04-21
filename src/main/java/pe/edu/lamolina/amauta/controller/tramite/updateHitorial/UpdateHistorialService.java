
package pe.edu.lamolina.amauta.controller.tramite.updateHitorial;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.TramiteCorreccionHistorial;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface UpdateHistorialService {

    public List<TramiteCorreccionHistorial> allByCiclo(CicloAcademico cicloAcademico, DynatableFilter filter);

    public void save(TramiteCorreccionHistorial correccionHistorial, DataSessionPivot ds);

    public void anular(TramiteCorreccionHistorial correccionHistorial, DataSessionPivot ds);
    
}
