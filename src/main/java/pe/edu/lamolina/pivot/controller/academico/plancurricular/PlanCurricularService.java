package pe.edu.lamolina.pivot.controller.academico.plancurricular;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.CursoCurricula;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.academico.OrientacionCarrera;
import pe.edu.lamolina.pivot.model.academico.PlanCurricular;
import pe.edu.lamolina.pivot.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

public interface PlanCurricularService {

    List<Carrera> allCarrerasByFilter(Facultad facultad, EstadoEnum estadoEnum);

    List<OrientacionCarrera> allOrientacionCarreraByFilter(Carrera carrera, EstadoEnum estadoEnum);

    PlanCurricular savePlanCurricular(PlanCurricular planCurricular);

    void agregarCursoCurricula(CursoCurricula cursoCurricula);

    PlanCurricular findPlanCurricularById(PlanCurricular planCurricular);

    List<CicloAcademico> allRecientesCiclosAcad(Integer year, Integer limit);

    List<PlanCurricular> allByDynatable(DynatableFilter filter, Facultad facultad);

    List<TipoCursoCurricula> allTiposCursoCurricula();

    List<CursoCurricula> allCursosCurriculaByFilter(TipoCursoCurricula tipoCursoCurricula);

    List<Curso> allCursoByNombre(String nombre);

    TipoCursoCurricula findTipoCurricula(Long tipoCursoCurricula);

    List<CursoCurricula> allCursosOblByDynatable(DynatableFilter filter);

    List<CursoCurricula> allCursoCurriculaByNombre(Long planCurriculaId, Integer numeroCiclo, String nombre);
}
