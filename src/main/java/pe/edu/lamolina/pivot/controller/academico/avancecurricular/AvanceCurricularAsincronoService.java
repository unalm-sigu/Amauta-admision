package pe.edu.lamolina.pivot.controller.academico.avancecurricular;

import java.util.List;
import java.util.Map;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.CursoEquivalente;
import pe.edu.lamolina.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.RequisitoCursoCurricula;
import pe.edu.lamolina.model.academico.ResumenPlanCurricular;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.matricula.AlumnoAvanceCurricular;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AvanceCurricularAsincronoService {

    void procesarAlumno(
            Alumno alumno,
            Map<Long, CursoCurricula> cursosCurricula,
            Map<Long, List<RequisitoCursoCurricula>> mapRequisitos,
            Map<Long, List<CursoEquivalente>> mapEquivalentes,
            Map<String, AlumnoCicloCurso> mapCursosVecesLlevado,
            List<MatriculaCurso> cursosMatriculados,
            List<AlumnoCicloCurso> cursosAprobadosAlumno,
            List<AlumnoCursoCurricula> alumnoCursoCurricula,
            List<CursoOpcionalCurricula> cursoOpcionalCurriculas,
            Map<Long, CursoCurricula> mapCursoCurriculaByCurso,
            List<TipoCursoCurricula> tipoCursoCurriculas,
            List<ResumenPlanCurricular> resumenPlanCurriculars,
            List<AlumnoAvanceCurricular> alumnoAvanceCurriculars,
            DataSessionPivot ds);

    void procesarAlumnoSincrono(
            Alumno alumno,
            Map<Long, CursoCurricula> cursosCurricula,
            Map<Long, List<RequisitoCursoCurricula>> mapRequisitos,
            Map<Long, List<CursoEquivalente>> mapEquivalentes,
            Map<String, AlumnoCicloCurso> mapCursosVecesLlevado,
            List<MatriculaCurso> cursosMatriculados,
            List<AlumnoCicloCurso> cursosAprobadosAlumno,
            List<AlumnoCursoCurricula> alumnoCursoCurricula,
            List<CursoOpcionalCurricula> cursoOpcional,
            Map<Long, CursoCurricula> mapCursosCurriculaByCurso,
            List<TipoCursoCurricula> tipoCursoCurriculas,
            List<ResumenPlanCurricular> resumenPlanCurriculars,
            List<AlumnoAvanceCurricular> alumnoAvanceCurriculars,
            DataSessionPivot ds);

    void deleteAllAlumnoCursoSimultaneoByAlumno(Alumno alumno);

    void deleteAllAlumnoCursoCurriculaByAlumno(Alumno alumno);

    void settingPlanCurricular(Alumno alumno, PlanCurricular planBD);

    void limpiarAlumno(Alumno alumno);

    void crearAvanceCurricular(
            Alumno alumno,
            PlanCurricular planBD,
            Map<Long, CursoCurricula> mapCursoCurricula,
            Map<Long, List<RequisitoCursoCurricula>> mapRequisitoCursoCurricula,
            Map<Long, List<CursoEquivalente>> mapCursosEquivalentes,
            Map<String, AlumnoCicloCurso> mapCursosVecesLlevado,
            List<MatriculaCurso> cursosMatriculados,
            List<AlumnoCicloCurso> cursosAprobadosAlumno,
            List<AlumnoCursoCurricula> alumnoCursoCurricula,
            List<CursoOpcionalCurricula> cursoOpcionalCurriculas,
            Map<Long, CursoCurricula> mapCursoCurriculaByCurso,
            List<TipoCursoCurricula> tipoCursoCurriculas,
            List<ResumenPlanCurricular> resumenPlanCurriculars,
            List<AlumnoAvanceCurricular> alumnoAvanceCurriculars,
            DataSessionPivot ds);
}
