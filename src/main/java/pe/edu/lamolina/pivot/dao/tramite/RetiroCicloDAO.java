package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.RetiroCiclo;

public interface RetiroCicloDAO extends EasyDAO<RetiroCiclo> {

    public List<RetiroCiclo> allByCiclo(CicloAcademico cicloAcademico, DynatableFilter filter);

    public RetiroCiclo findByAlumnoCicloRegistro(Alumno alumno, CicloAcademico ciclo);

    public List<RetiroCiclo> allByCiclo(CicloAcademico ciclo);

    public List<RetiroCiclo> allAlumnosByCiclo(List<Long> alumnos, CicloAcademico ciclo);
    
    public RetiroCiclo findByAlumnoCicloRetiro(Alumno alumno, CicloAcademico ciclo);

    public List<RetiroCiclo> allByRetiroCiclo(Alumno alumno);

}
