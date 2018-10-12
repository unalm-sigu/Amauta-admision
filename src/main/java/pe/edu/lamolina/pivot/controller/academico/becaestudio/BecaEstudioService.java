package pe.edu.lamolina.pivot.controller.academico.becaestudio;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.BecaEstudio;

public interface BecaEstudioService {

    List<BecaEstudio> allBecaEstudio(DynatableFilter filter);
    

}
