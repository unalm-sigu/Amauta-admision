package pe.edu.lamolina.pivot.controller.soporte;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Soporte;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface SoporteService {

    public void responder(Soporte soporte, DataSessionPivot ds);

    public List<Soporte> list(DynatableFilter filter);

    


}
