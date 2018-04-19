package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.pivot.controller.academico.carrera.CarreraResumen;

public interface CarreraDAO extends EasyDAO<Carrera> {

    Carrera findByCodigo(String cod);

    List<Carrera> allByDynatable(DynatableFilter filter);

    List<Carrera> allByCompania(Compania compania);

    Carrera find(Long id);

    List<Carrera> allByNombre(String forLike);

    List<Carrera> allByFilter(Facultad facultad, EstadoEnum estadoEnum);

    List<Carrera> allByModalidadEstudioNombre(String idModEstudio, String forLike);

    CarreraResumen resumen();

    List<Carrera> allCarrera();

    List<Carrera> allCarreraByName(String nombre, ModalidadEstudio modalidadEstudio);

    List<Carrera> allCarreraByModalidadEstudio(ModalidadEstudio modalidadEstudio);

    List<Carrera> allRegularesByCarreras(List<Carrera> carreras);

    List<Carrera> allActivoByModalidad(ModalidadEstudio modalidadEstudio);

    List<Carrera> allCarreraByNameAndModalidad(String nombre, List<ModalidadEstudio> modalidadEstudio);

    List<Carrera> allCarreraByName(String nombre, Compania cia);

    List<Carrera> allActivos();

    List<Carrera> allActivasByModalidadesEstudio(List<String> modalidadesCodes);

    List<Carrera> allByModalidadEstudio(ModalidadEstudioEnum modalidadEstudioEnum);

}
