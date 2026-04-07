package pe.edu.lamolina.amauta.dao.academico;

import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.RegistroBorradoAlumno;

import java.util.List;

public interface RegistroBorradoAlumnoDAO extends EasyDAO<RegistroBorradoAlumno> {

    List<RegistroBorradoAlumno> allByDynatable(DynatableFilter filter);
}
