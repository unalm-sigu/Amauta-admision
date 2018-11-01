package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.DeudaMaterialAlumno;
import pe.edu.lamolina.model.academico.TipoDeudaMaterial;

public interface DeudaAlumnoDAO extends EasyDAO<DeudaMaterialAlumno> {

    List<DeudaMaterialAlumno> allByDynatableTipoDeuda(DynatableFilter filter, TipoDeudaMaterial tipo);

    DeudaMaterialAlumno findByTipoAlumno(TipoDeudaMaterial tipo, Alumno alumno);

}
