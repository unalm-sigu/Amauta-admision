package pe.edu.lamolina.pivot.controller.academico.silabo;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.SilaboCurso;

public interface SilaboService {

    List<SilaboCurso> allSilabo(DynatableFilter filter);

}
