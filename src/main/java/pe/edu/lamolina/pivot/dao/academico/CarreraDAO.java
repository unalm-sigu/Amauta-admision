package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.general.Compania;

public interface CarreraDAO extends Crud<Carrera> {

    Carrera findByCodigo(String cod);

    List<Carrera> allByCompania(Compania compania);

}
