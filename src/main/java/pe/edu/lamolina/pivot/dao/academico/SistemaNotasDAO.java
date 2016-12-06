package pe.edu.lamolina.pivot.dao.academico;

import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.SistemaNotas;

public interface SistemaNotasDAO extends Crud<SistemaNotas> {

    SistemaNotas find(Long id);

}
