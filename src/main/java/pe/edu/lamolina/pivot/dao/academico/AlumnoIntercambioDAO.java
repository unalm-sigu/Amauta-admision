package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.academico.AlumnoIntercambio;

public interface AlumnoIntercambioDAO extends Crud<AlumnoIntercambio> {

    public AlumnoIntercambio find(AlumnoIntercambio alumnoBecado);

    public List<AlumnoIntercambio> allByDynatable(DynatableFilter filter);

}
