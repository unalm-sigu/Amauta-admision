package pe.edu.lamolina.amauta.dao.matricula;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.MatriculaBloqueoAlumno;

public interface MatriculaBloqueoAlumnoDAO extends EasyDAO<MatriculaBloqueoAlumno> {

    public List<MatriculaBloqueoAlumno> allDynatable(DynatableFilter filter);

}
