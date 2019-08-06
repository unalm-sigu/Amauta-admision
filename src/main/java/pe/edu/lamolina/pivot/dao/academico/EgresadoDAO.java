package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ControlMeritoEgresado;
import pe.edu.lamolina.model.academico.Egresado;

public interface EgresadoDAO extends EasyDAO<Egresado> {

    Egresado findByAlumno(Alumno alumno);

    Egresado findPrincipalByAlumno(Alumno alumno);

    List<Egresado> allByCicloAcademico(CicloAcademico ciclo);

    List<Egresado> allByControlesOrdenMerito(List<ControlMeritoEgresado> coms);

    void deleteInfoOrdenMeritoByCicloAcademico(CicloAcademico cicloAcademico);

    List<Egresado> allByControlMeritoCiclo(DynatableFilter filter, ControlMeritoEgresado controlBD);

    List<Egresado> allByControlMeritoCarrera(DynatableFilter filter, ControlMeritoEgresado controlBD);

    List<Egresado> allByControlMeritoFacultad(DynatableFilter filter, ControlMeritoEgresado controlBD);

    public List<Egresado> allByAlumnos(List<Alumno> alumnos);

}
