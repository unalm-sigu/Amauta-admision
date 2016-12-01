package pe.edu.lamolina.pivot.dao.academico;

import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.Docente;

public interface DocenteDAO extends Crud<Docente> {

    Docente find(Long idDocente);

}
