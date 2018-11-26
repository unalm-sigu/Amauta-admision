package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.rolexamen.RolExamenes;

public interface CursoDAO extends EasyDAO<Curso> {

    List<Curso> allForSistemaCalificacion(String nombre, DepartamentoAcademico departamento, PlanCalificacion planCalificacion, CicloAcademico ciclo);

    List<Curso> allByPlan(PlanCalificacion plan);

    List<Curso> allByPlanRegular(PlanCalificacion plan);

    List<Curso> allActiveByPlan(PlanCalificacion plan);

    Curso findByCode(String codigo);

    List<Curso> allByDynatable(DynatableFilter filter, List<DepartamentoAcademico> departamentos);

    List<Curso> allByNombreTipoCurricula(String nombre, List<String> tiposCurriculaEnum, Integer limit);

    List<Curso> allByCodigo(String codigo);

    Curso findLastByCodigoFacultad(String codigo);

    List<Curso> allForProgramacion(String nombre);

    List<Curso> allCursoByName(String nombre);

    List<Curso> allCursoCachimbosByCicloAcademico(CicloAcademico cicloAcademico, Carrera carrera);

    List<Curso> allCursoByNameExceptList(String nombre, List<Curso> cursos);

    List<Curso> allByDptoEstado(Long idDpto, String estado);

    List<Curso> allActiveByCodigo(String codigo, CicloAcademico ciclo);

    List<Curso> allByModalidadEstudioNombre(ModalidadEstudioEnum moda, String nombre);

    List<Curso> allNoEncuestar();

    List<Curso> searchLikeNombre(String nombre, Integer limit);

    List<Curso> allForExamenByCiclo(String nombre, RolExamenes rolExamenes, CicloAcademico cicloAcademico);
}
