package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.general.Oficina;
import pe.edu.lamolina.pivot.model.general.Persona;

public interface OficinaDAO extends Crud<Oficina> {

    List<Oficina> allByJefe(Persona persona);

}
