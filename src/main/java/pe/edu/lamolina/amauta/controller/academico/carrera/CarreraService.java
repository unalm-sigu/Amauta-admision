package pe.edu.lamolina.amauta.controller.academico.carrera;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.AreaPosgrado;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.OrientacionCarrera;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface CarreraService {

    List<Carrera> allByDynatable(DynatableFilter filter);

    void cambiarEstadoCarrera(Carrera carrera);

    List<ModalidadEstudio> allPrePostgrado(Compania cia);

    Carrera save(Carrera carrera, DataSessionPivot ds);

    List<Facultad> allFacultades();

    Carrera find(Long id);

    //void saveOrientacion(Long idCarrera, Long idOrientacion, String nombreOrientacion, Usuario usuario);

    List<OrientacionCarrera> saveOrientaciones(Carrera carrera, DataSessionPivot ds);

//    void deleteOrientacion(Long idOrientacion);

    OrientacionCarrera deleteOrientacion(OrientacionCarrera orientacion, DataSessionPivot ds);

    OrientacionCarrera activarOrientacion(OrientacionCarrera orientacion, DataSessionPivot ds);

//    List<OrientacionCarrera> allByIdCarreraDynatable(DynatableFilter filter, Long idCarrera);

    OrientacionCarrera editarOrientacion(OrientacionCarrera orientacion, DataSessionPivot ds);

    CarreraResumen resumen();

    List<Carrera> all();

    void cambiarEstadoAdmision(Carrera carrera);

    List<AreaPosgrado> allAreaPosgrado();

}
