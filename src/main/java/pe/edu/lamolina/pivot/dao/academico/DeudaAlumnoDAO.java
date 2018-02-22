package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.DeudaAlumno;
import pe.edu.lamolina.model.academico.TipoDeudaAlumno;

public interface DeudaAlumnoDAO extends EasyDAO<DeudaAlumno> {

    List<DeudaAlumno> allByDynatable(DynatableFilter filter);

    DeudaAlumno findByTipoAlumno(TipoDeudaAlumno tipo, Alumno alumno);

}
