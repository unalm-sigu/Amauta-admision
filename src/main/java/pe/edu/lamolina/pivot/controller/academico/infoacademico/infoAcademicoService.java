package pe.edu.lamolina.pivot.controller.academico.infoacademico;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface infoAcademicoService {

    ObjectNode allAlumnosByCiclo(Alumno alumno, Long numeroCiclo);

    ObjectNode allAlumnosByCursosMatri(Alumno alumno, CicloAcademico cicloAca);

    List<MatriculaCurso> allCursosMatriculadosByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    List<Hora> allHoras();

    Alumno allInfo(Alumno alumno);

    void generarAvance(Alumno alumno, DataSessionPivot ds);

    List<AlumnoCicloCurso> allPromediosByAlumnoOrderByCurso(Alumno alumno);

    List<AlumnoCiclo> allPromediosByAlumno(Alumno alumno);
    
    List<PlanCurricular> allPlanCurricularByCarrera(Carrera carrera);
    
    void cambiarPlan(Alumno alumno, PlanCurricular planCurricular);

}