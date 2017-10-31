package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.general.Compania;

public interface CarreraDAO extends Crud<Carrera> {

    Carrera findByCodigo(String cod);

    public List<Carrera> allByDynatable(DynatableFilter filter);

    List<Carrera> allByCompania(Compania compania);

    Carrera find(Long id);

}
