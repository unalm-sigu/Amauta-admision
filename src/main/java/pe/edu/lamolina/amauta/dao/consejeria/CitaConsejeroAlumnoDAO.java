package pe.edu.lamolina.amauta.dao.consejeria;

import java.util.Date;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tutoria.CitaConsejeroAlumno;

public interface CitaConsejeroAlumnoDAO extends EasyDAO<CitaConsejeroAlumno> {

    CitaConsejeroAlumno findUltimoByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    List<CitaConsejeroAlumno> allByDynatable(DynatableFilter filter, Alumno alumno, CicloAcademico ciclo);

    List<CitaConsejeroAlumno> allByAlumnoFecha(Alumno alumno, Date fecha);

    List<CitaConsejeroAlumno> allUltimosByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    List<CitaConsejeroAlumno> allUltimosByAlumnosCiclo(List<Alumno> alumnos, CicloAcademico ciclo);

    List<CitaConsejeroAlumno> allByAlumnosCiclo(List<Alumno> alumnos, CicloAcademico ciclo);

}
