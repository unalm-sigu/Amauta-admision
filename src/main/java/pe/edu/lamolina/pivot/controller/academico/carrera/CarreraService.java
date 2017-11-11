package pe.edu.lamolina.pivot.controller.academico.carrera;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.academico.OrientacionCarrera;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;

public interface CarreraService {

    List<Carrera> allByDynatable(DynatableFilter filter);

    void cambiarEstadoCarrera(Carrera carrera);

    List<ModalidadEstudio> allPrePostgrado(Compania cia);

    void save(Carrera carrera, Usuario usuario);

    List<Facultad> allFacultades();

    Carrera find(Long id);

    void saveOrientacion(Long idCarrera, Long idOrientacion, String nombreOrientacion, Usuario usuario);

    void deleteOrientacion(Long idOrientacion);

    void cambioEstado(OrientacionCarrera orientacion);

    List<OrientacionCarrera> allByIdCarreraDynatable(DynatableFilter filter, Long idCarrera);

    OrientacionCarrera editarOrientacion(Long id);

}
