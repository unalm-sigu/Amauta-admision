package pe.edu.lamolina.amauta.dao.mensajeria;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.GrupoAlumno;


public interface GrupoAlumnoDAO extends EasyDAO<GrupoAlumno> {

    List<GrupoAlumno> allByDynatble(DynatableFilter filter);

}
