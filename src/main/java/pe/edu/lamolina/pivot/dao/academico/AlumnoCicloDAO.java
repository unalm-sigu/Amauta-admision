package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;

public interface AlumnoCicloDAO extends EasyDAO<AlumnoCiclo> {

    List<AlumnoCiclo> allByCicloAcademicoPlanCurricular(PlanCurricular plan, CicloAcademico ciclo);

    Long countByCicloAcademicoPlanCurricular(CicloAcademico cicloAcademico, PlanCurricular planCurricular);

    AlumnoCiclo findByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico);

    List<AlumnoCiclo> allByAlumno(Alumno alumno);

    AlumnoCiclo findLastByAlumno(Alumno alumno);

    AlumnoCiclo findActiveAnteriorByAlumno(Alumno alumno, CicloAcademico cicloAcademico);

    List<AlumnoCiclo> allActivesByAlumnoAsc(Alumno alumno);

    AlumnoCiclo findActiveByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico);

    AlumnoCiclo findByAlumnoCicloEstado(Alumno alumno, CicloAcademico cicloAcademico, List<EstadoMatriculaEnum> estadosEnums);

    Long countCiclosEstudiados(Alumno alumno, CicloAcademico cicloAcademico);
}
