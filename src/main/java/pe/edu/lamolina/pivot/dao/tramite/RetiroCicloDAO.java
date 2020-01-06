package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.model.tramite.Tramite;

public interface RetiroCicloDAO extends EasyDAO<RetiroCiclo> {

    List<RetiroCiclo> allByCiclo(CicloAcademico cicloAcademico, DynatableFilter filter);

    RetiroCiclo findByAlumnoCicloRegistro(Alumno alumno, CicloAcademico ciclo);

    List<RetiroCiclo> allByCiclo(CicloAcademico ciclo);

    List<RetiroCiclo> allAlumnosByCiclo(List<Alumno> alumnos, CicloAcademico ciclo);

    RetiroCiclo findByAlumnoCicloRetiro(Alumno alumno, CicloAcademico ciclo);

    List<RetiroCiclo> allByRetiroCiclo(Alumno alumno);

    List<RetiroCiclo> allByResolucion(Resolucion resolucion);

    List<RetiroCiclo> allByTramites(List<Tramite> tramites);

    RetiroCiclo findByTramite(Tramite tramite);

    List<RetiroCiclo> allInfo();

    List<RetiroCiclo> allRetiroCicloByAlumno(Alumno alumno);

}
