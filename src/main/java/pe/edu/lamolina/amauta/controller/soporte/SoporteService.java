package pe.edu.lamolina.amauta.controller.soporte;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Soporte;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface SoporteService {

    public void responder(Soporte soporte, DataSessionPivot ds);

    public List<Soporte> list(DynatableFilter filter);

    


}
