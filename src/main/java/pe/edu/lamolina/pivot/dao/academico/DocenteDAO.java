package pe.edu.lamolina.pivot.dao.academico;

import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.general.Persona;

public interface DocenteDAO extends Crud<Docente> {

    Docente find(Long idDocente);

    Docente findPersona(Persona persona);

    Docente findByCode(String codigo);

}
