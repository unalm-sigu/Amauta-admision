
package pe.edu.lamolina.pivot.controller.tramite.updateHitorial;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.TramiteCorreccionHistorial;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface UpdateHistorialService {

    public List<TramiteCorreccionHistorial> allByCiclo(CicloAcademico cicloAcademico, DynatableFilter filter);

    public void save(MultipartFile file, Long id, DataSessionPivot ds);

    public void anular(TramiteCorreccionHistorial correccionHistorial, DataSessionPivot ds);
    
}
