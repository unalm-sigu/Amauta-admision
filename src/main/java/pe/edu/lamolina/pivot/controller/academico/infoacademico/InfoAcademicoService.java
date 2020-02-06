package pe.edu.lamolina.pivot.controller.academico.infoacademico;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.OrientacionCarrera;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.aporte.BoletaIngresante;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.model.tramite.RetiroCurso;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface InfoAcademicoService {

    Alumno findAlumno(Long idAlumno);

    ObjectNode allAvanceCurricular(Alumno alumno);

    List<MatriculaCurso> allCursosMatriculadosByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    List<Hora> allHoras();

    Alumno findWithallInfo(Alumno alumno);

    void generarAvance(Alumno alumno, DataSessionPivot ds);

    List<AlumnoCicloCurso> allHistorialAlumno(Alumno alumno);

    ArrayNode allPromediosJson(List<AlumnoCicloCurso> cursosCiclos);

    ArrayNode allCursosJson(List<AlumnoCicloCurso> cursosCiclosOrigen);

    List<PlanCurricular> allPlanCurricularByAlumno(Alumno alumno);

    void cambiarPlan(Alumno alumno, PlanCurricular planCurricular, DataSessionPivot ds);

    void calcularPromedio(Alumno alumno, DataSessionPivot ds);

    List<BoletaIngresante> allAportesAlumno(Alumno alumno, CicloAcademico cicloAcademico);

    MatriculaResumen findResumenMatricula(Alumno alumno, CicloAcademico ciclo);

    MatriculaResumen findResumenMatricula(Alumno alumno, CicloAcademico ciclo, List<MatriculaCurso> matriculaCursos);

    List<HorarioSeccion> allSeccionHorarioAlumnoByAlumnoCicloACademico(Alumno alumno, CicloAcademico academico);

    List<HorarioSeccion> allSeccionHorarioAlumnoByDocenteCicloACademico(Docente docente, CicloAcademico academico);

    ObjectNode findHorarioBySeccionesHorarios(List<HorarioSeccion> seccionesHorarios);

    Hora getHoraByNroHora(Integer numero);

    void cambiarOrientacion(Alumno alumno, OrientacionCarrera orientacionCarrera, DataSessionPivot ds);

    boolean usuarioPuedeCalcular(DataSessionPivot ds);

    List<RetiroCiclo> allRetiroCicloByAlumno(Alumno alumno);

    List<RetiroCurso> allRetiroCursoByAlumno(Alumno alumno);

    ObjectNode allDataAlumnoMerito(Alumno alumno);

    Alumno aplicarRetiroCiclo(RetiroCiclo retiro, DataSessionPivot ds);

    public String getToken();

    public String getUrl();

}
