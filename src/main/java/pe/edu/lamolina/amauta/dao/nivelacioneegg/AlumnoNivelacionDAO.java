package pe.edu.lamolina.amauta.dao.nivelacioneegg;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnosnivelacion.dto.AlumnosNivelacionResumen;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.nivelacioneegg.AlumnoNivelacion;

public interface AlumnoNivelacionDAO extends EasyDAO<AlumnoNivelacion> {

    List<AlumnoNivelacion> allByCiclo(CicloAcademico ciclo);

    List<AlumnoNivelacion> allByDynatable(DynatableFilter filter, CicloAcademico ciclo, List<Carrera> carreras, String todo);

    AlumnoNivelacion findByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    AlumnosNivelacionResumen findResumen(CicloAcademico ciclo);

}
