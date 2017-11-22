package pe.edu.lamolina.pivot.controller.academico.plancurricular;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.CursoAdicionalCurricula;
import pe.edu.lamolina.pivot.model.academico.CursoCurricula;
import pe.edu.lamolina.pivot.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.academico.OrientacionCarrera;
import pe.edu.lamolina.pivot.model.academico.PlanCurricular;
import pe.edu.lamolina.pivot.model.academico.ResumenPlanCurricular;
import pe.edu.lamolina.pivot.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoCurriculaEnum;

public interface PlanCurricularService {

    List<Carrera> allCarrerasByFilter(Facultad facultad, EstadoEnum estadoEnum);

    List<OrientacionCarrera> allOrientacionCarreraByFilter(Carrera carrera, EstadoEnum estadoEnum);

    PlanCurricular savePlanCurricular(PlanCurricular planCurricular);

    void agregarCursoCurricula(CursoCurricula cursoCurricula);

    PlanCurricular findPlanCurricularById(PlanCurricular planCurricular);

    List<CicloAcademico> allRecientesCiclosAcad(Integer year, Integer limit);

    List<PlanCurricular> allByDynatable(DynatableFilter filter, List<Carrera> carreras);

    List<TipoCursoCurricula> allTiposCursoCurricula();

    List<CursoCurricula> allCursosCurriculaByFilter(TipoCursoCurricula tipoCursoCurricula);

    List<Curso> allCursoByNombreTipoCurricula(String nombre, List<TipoCurriculaEnum> tiposCurriculaEnum);

    TipoCursoCurricula findTipoCurricula(Long tipoCursoCurricula);

    List<CursoCurricula> allCursosOblByDynatable(DynatableFilter filter);

    List<CursoCurricula> allCursoCurriculaByNombre(Long planCurriculaId, Integer numeroCiclo, String nombre);

    CursoCurricula findCursoCurricula(Long cursoCurricula);

    void updateCursoCurricula(CursoCurricula cursoCurricula);

    void agregarCursoAdcCurricula(CursoAdicionalCurricula cursoAdicionalCurricula);

    List<CursoAdicionalCurricula> allCursosAdcByDynatable(DynatableFilter filter);

    void deleteCursoAdicional(Long cursoAdicionalId);

    void agregarCursoOpcCurricula(CursoOpcionalCurricula cursoOpcionalCurricula);

    List<CursoOpcionalCurricula> allCursosElecByDynatable(DynatableFilter filter);

    void deleteCursoOpcional(Long cursoOpcionalId);

    List<Curso> allCursosByCodigo(String codigo);

    List<ResumenPlanCurricular> allResPlanCurByDynatable(DynatableFilter filter);

}
