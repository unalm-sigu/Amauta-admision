package pe.edu.lamolina.pivot.dao.consejeria;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.consejeria.consejeria.ConsejeroEstado;

public interface ConsejeroDAO extends EasyDAO<Consejero> {

    List<Consejero> allByCarreraDynatable(Carrera carrera, DynatableFilter filter);

    List<DepartamentoAcademico> allByIdFacultad(String facultadid);

    List<Docente> allByNombreAndDeparts(String nombre, List<DepartamentoAcademico> departs);

    Consejero finByIdPersona(Persona persona);

    List<Carrera> findAllCarreraByIdDocente(long idDocente);

    ConsejeroEstado findByStateCarrera(Long carrera);

    List<Consejero> findConsejeroByEstado(Long carrera);

    List<Consejero> allByNombreAndCarrera(String nombre, Carrera carrera);

}
