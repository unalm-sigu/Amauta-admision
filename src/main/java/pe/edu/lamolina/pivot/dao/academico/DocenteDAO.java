package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.general.Persona;

public interface DocenteDAO extends Crud<Docente> {

    Docente find(Long idDocente);

    Docente findPersona(Persona persona);

    Docente findByCode(String codigo);

    List<Docente> allByPersona(Persona persona);

    List<Docente> allActivos(ModalidadEstudio modalidad);

    List<Docente> allByFilter(DynatableFilter filter);

    Docente findDocente(Docente docente);

    public Docente findDocenteByPersona(Persona persona);

    public List<Docente> allCoordinadoresByIdDptoName(Long idDpto, String nombre);

}
