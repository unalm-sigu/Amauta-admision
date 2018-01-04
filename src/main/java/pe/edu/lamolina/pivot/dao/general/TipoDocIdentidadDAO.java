package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.general.TipoDocIdentidad;

public interface TipoDocIdentidadDAO extends Crud<TipoDocIdentidad> {

    public List<TipoDocIdentidad> allForPersonaNatural();

}

