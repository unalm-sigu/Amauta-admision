package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.general.PerfilCompania;

public interface PerfilCompaniaDAO extends Crud<PerfilCompania> {

    public List<PerfilCompania> allByNombre(String nombre);

}

