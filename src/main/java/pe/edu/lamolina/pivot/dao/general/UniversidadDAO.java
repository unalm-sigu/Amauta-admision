package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.general.Universidad;

public interface UniversidadDAO extends Crud<Universidad> {

    public List<Universidad> allUniversidadByName(String nombre);

}

