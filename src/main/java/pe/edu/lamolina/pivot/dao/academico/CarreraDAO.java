package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.controller.academico.carrera.CarreraResumen;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

public interface CarreraDAO extends Crud<Carrera> {

    Carrera findByCodigo(String cod);

    public List<Carrera> allByDynatable(DynatableFilter filter);

    List<Carrera> allByCompania(Compania compania);

    Carrera find(Long id);

    List<Carrera> allByNombre(String forLike);

    List<Carrera> allByFilter(Facultad facultad, EstadoEnum estadoEnum);

    List<Carrera> allByModalidadEstudioNombre(String idModEstudio, String forLike);

    CarreraResumen resumen();

    List<Carrera> allCarrera();

    List<Carrera> allCarreraByName(String nombre, ModalidadEstudio modalidadEstudio);

    List<Carrera> allCarreraByModalidadEstudio(ModalidadEstudio modalidadEstudio);

}
