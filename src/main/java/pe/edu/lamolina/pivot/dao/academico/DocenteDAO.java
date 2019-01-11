package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.general.Persona;

public interface DocenteDAO extends EasyDAO<Docente> {

    List<Docente> allByFacultadesDyantable(DynatableFilter filter, List<DepartamentoAcademico> departamento);

    Docente findByCode(String codigo);

    List<Docente> allByPersona(Persona persona);

    List<Docente> allActivos(ModalidadEstudio modalidad);

    List<Docente> allByFilter(DynatableFilter filter, List<DepartamentoAcademico> dptos);

    Docente findByDocente(Docente docente);

    List<Docente> allCoordinadoresByIdDptoName(Long idDpto, String nombre);

    List<Docente> allByNombreFilter(String nombre, Integer limit, String codigoDep);

    List<Docente> allByDptoEstado(Long idDpto, String estado);

    List<Docente> allByNombreDepartamento(String nombre, DepartamentoAcademico departamento, int limit);

    public List<Docente> allByName(String nombre);

}
