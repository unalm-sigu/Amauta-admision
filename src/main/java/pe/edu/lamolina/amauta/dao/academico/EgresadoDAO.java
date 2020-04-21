package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ControlMeritoEgresado;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.amauta.controller.academico.egresado.EgresadoResumen;

public interface EgresadoDAO extends EasyDAO<Egresado> {

    Egresado findByAlumno(Alumno alumno);

    Egresado findPrincipalByAlumno(Alumno alumno);

    List<Egresado> allByCicloAcademico(CicloAcademico ciclo);

    List<Egresado> allByControlesOrdenMerito(List<ControlMeritoEgresado> coms);

    void deleteInfoOrdenMeritoByCicloAcademico(CicloAcademico cicloAcademico);

    List<Egresado> allByControlMeritoCiclo(DynatableFilter filter, ControlMeritoEgresado controlBD);

    List<Egresado> allByControlMeritoCarrera(DynatableFilter filter, ControlMeritoEgresado controlBD);

    List<Egresado> allByControlMeritoFacultad(DynatableFilter filter, ControlMeritoEgresado controlBD);

    List<Egresado> allByAlumnos(List<Alumno> alumnos);

    List<Egresado> allByCarrerasDynatable(DynatableFilter filter, List<Carrera> carreras, String todo);

    EgresadoResumen findResumenEgresado(List<Carrera> carreras, String todo);

    List<Egresado> allForPdfByCicloAcademico(CicloAcademico cicloAcademico);

}
