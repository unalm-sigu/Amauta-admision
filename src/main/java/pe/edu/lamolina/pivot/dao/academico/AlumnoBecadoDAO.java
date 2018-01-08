package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.academico.AlumnoBecado;

public interface AlumnoBecadoDAO extends Crud<AlumnoBecado> {

    public AlumnoBecado findAlumnoBecado(AlumnoBecado alumnoBecado);

    public List<AlumnoBecado> allByDynatable(DynatableFilter filter);

}
