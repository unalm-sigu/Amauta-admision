package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.general.Sede;

public interface SedeDAO extends Crud<Sede> {

    public List<Sede> allSedesByName(String nombre);

}

