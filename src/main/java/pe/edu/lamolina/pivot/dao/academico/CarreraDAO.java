package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.Carrera;

public interface CarreraDAO extends Crud<Carrera> {

    Carrera findByCodigo(String cod);

    public List<Carrera> allByModalidadEstudio(DynatableFilter filter);

}
