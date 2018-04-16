package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.general.Persona;

public interface DocenteDAO extends EasyDAO<Docente> {

    Docente findByCode(String codigo);

    List<Docente> allByPersona(Persona persona);

    List<Docente> allActivos(ModalidadEstudio modalidad);

    List<Docente> allByFilter(DynatableFilter filter);

    Docente findByDocente(Docente docente);

    List<Docente> allCoordinadoresByIdDptoName(Long idDpto, String nombre);

    List<Docente> allByNombreFilter(String nombre, Integer limit);

}
