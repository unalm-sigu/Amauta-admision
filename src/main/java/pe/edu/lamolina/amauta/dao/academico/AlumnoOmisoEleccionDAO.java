package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoOmisoEleccion;
import pe.edu.lamolina.model.academico.CicloAcademico;

public interface AlumnoOmisoEleccionDAO extends EasyDAO<AlumnoOmisoEleccion> {

    List<AlumnoOmisoEleccion> allOrder(DynatableFilter filter);

    void updateAnulacion(AlumnoOmisoEleccion alumnoOmisoEleccion);

    List<AlumnoOmisoEleccion> allByCiclo(List<CicloAcademico> cicloAcademicos);

    AlumnoOmisoEleccion findByAlumnoCicloMotivo(AlumnoOmisoEleccion omisoEleccion);

    List<AlumnoOmisoEleccion> allDeudasByAlumno(Alumno alumno);

}
