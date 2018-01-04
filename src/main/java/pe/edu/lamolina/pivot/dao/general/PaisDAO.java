package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.general.Pais;

public interface PaisDAO extends Crud<Pais> {

    public List<Pais> allPaisesByName(String forLike);

}

