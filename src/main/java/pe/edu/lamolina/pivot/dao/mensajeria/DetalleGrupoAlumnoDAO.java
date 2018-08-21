package pe.edu.lamolina.pivot.dao.mensajeria;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.DetalleGrupoAlumno;
import pe.edu.lamolina.model.academico.GrupoAlumno;

public interface DetalleGrupoAlumnoDAO extends EasyDAO<DetalleGrupoAlumno> {

    List<DetalleGrupoAlumno> allByDynatbleGrupoAlumno(DynatableFilter filter, GrupoAlumno grupo);

}
