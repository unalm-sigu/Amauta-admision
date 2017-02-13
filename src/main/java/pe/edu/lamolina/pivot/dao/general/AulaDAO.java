package pe.edu.lamolina.pivot.dao.general;

import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.general.Aula;

public interface AulaDAO extends Crud<Aula> {

    Aula findByCode(String codigo);

}
