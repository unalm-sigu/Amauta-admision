package pe.edu.lamolina.pivot.controller.academico.carrera;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.academico.OrientacionCarrera;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;

public interface CarreraService {

    List<Carrera> allByDynatable(DynatableFilter filter);

    void desactivar(Carrera carrera);

    List<ModalidadEstudio> allModalidades();

    void save(Carrera carrera, Usuario usuario);

    List<Facultad> allFacultades();

    Carrera find(Long id);

    void saveOrientacion(Long idCarrera, String nombreOrientacion, Usuario usuario);

    void deleteOrientacion(Long idOrientacion);

    void desactivarOrientacion(OrientacionCarrera orientacion);

    List<OrientacionCarrera> allByIdCarreraDynatable(DynatableFilter filter, Long idCarrera);

}
