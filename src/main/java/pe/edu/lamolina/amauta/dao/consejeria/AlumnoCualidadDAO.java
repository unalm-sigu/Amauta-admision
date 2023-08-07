package pe.edu.lamolina.amauta.dao.consejeria;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.tutoria.AlumnoCualidad;

public interface AlumnoCualidadDAO extends EasyDAO<AlumnoCualidad> {

    List<AlumnoCualidad> allByAlumno(Alumno alumno);

    List<AlumnoCualidad> allByAlumnos(List<Alumno> alumnos);

    List<AlumnoCualidad> allByAlumnoTipoCualidad(Alumno alumno, String tipoCualidad);

}
