package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.DeudaMaterialAlumno;
import pe.edu.lamolina.model.general.Oficina;

public interface DeudaMaterialAlumnoDAO extends EasyDAO<DeudaMaterialAlumno> {

    List<DeudaMaterialAlumno> allByDynatableTipoDeuda(DynatableFilter filter, List<Oficina> oficina);

    DeudaMaterialAlumno findByTipoAlumno(Oficina oficina, Alumno alumno);

}
