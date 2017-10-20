package pe.edu.lamolina.pivot.controller.academico.carrera;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;

public interface CarreraService {

    List<Carrera> allByDynatable(DynatableFilter filter);

    void desactivar(Carrera carrera);

    List<ModalidadEstudio> allModalidades();

}
