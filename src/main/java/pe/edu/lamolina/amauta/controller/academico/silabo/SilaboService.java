package pe.edu.lamolina.amauta.controller.academico.silabo;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.academico.SilaboCurso;

public interface SilaboService {

    List<SilaboCurso> allSilabo(DynatableFilter filter);

    void save(SilaboCurso silabo);

    void delete(SilaboCurso silabo);

    void revision(SilaboCurso silabo, JsonResponse response);

}
