package pe.edu.lamolina.pivot.controller.academico.infoacademico;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.OrientacionCarrera;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.aporte.BoletaIngresante;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface infoAcademicoService {

    Alumno findAlumno(Long idAlumno);

    ObjectNode allAvanaceCurricular(Alumno alumno);

    ObjectNode allAlumnosByCursosMatri(Alumno alumno, CicloAcademico cicloAca);

    List<MatriculaCurso> allCursosMatriculadosByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    List<Hora> allHoras();

    Alumno findWithallInfo(Alumno alumno);

    void generarAvance(Alumno alumno, DataSessionPivot ds);

    List<AlumnoCicloCurso> allPromediosByAlumnoOrderByCurso(Alumno alumno);

    List<AlumnoCiclo> allPromediosByAlumno(Alumno alumno);

    List<PlanCurricular> allPlanCurricularByAlumno(Alumno alumno);

    void cambiarPlan(Alumno alumno, PlanCurricular planCurricular, DataSessionPivot ds);

    void calcularPromedio(Alumno alumno, DataSessionPivot ds);

    List<BoletaIngresante> allAportesAlumno(Alumno alumno, CicloAcademico cicloAcademico);

    MatriculaResumen findResumenMatricula(Alumno alumno, CicloAcademico ciclo);

    List<HorarioSeccion> allSeccionHorarioAlumnoByAlumnoCicloACademico(Alumno alumno, CicloAcademico academico);

    ObjectNode findHorarioBySeccionesHorarios(List<HorarioSeccion> seccionesHorarios);

    Hora getHoraByNroHora(Integer numero);

    void cambiarOrientacion(Alumno alumno, OrientacionCarrera orientacionCarrera, DataSessionPivot ds);

}
