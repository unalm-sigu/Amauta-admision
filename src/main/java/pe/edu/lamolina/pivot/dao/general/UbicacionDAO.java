package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.general.Ubicacion;

public interface UbicacionDAO extends Crud<Ubicacion> {

    public List<Ubicacion> allDistritos(String nombre);

}

