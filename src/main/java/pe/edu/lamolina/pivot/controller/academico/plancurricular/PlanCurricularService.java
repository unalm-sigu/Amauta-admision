package pe.edu.lamolina.pivot.controller.academico.plancurricular;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoAdicionalCurricula;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.model.academico.OrientacionCarrera;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.RequisitoCursoOpcional;
import pe.edu.lamolina.model.academico.ResumenPlanCurricular;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.TipoCurriculaEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface PlanCurricularService {

    List<Carrera> allCarreras(List<Carrera> carreras);

    List<OrientacionCarrera> allOrientacionByCarreraEstado(Carrera carrera, EstadoEnum estadoEnum);

    PlanCurricular savePlanCurricular(PlanCurricular planCurricular);

    void saveCursoCurricula(CursoCurricula cursoCurricula, DataSessionPivot ds);

    void deleteCursoCurricula(CursoCurricula cursoCurricula, DataSessionPivot ds);

    void trasladarCiclo(CursoCurricula cursoCurricula, DataSessionPivot ds);

    void trasladarToElectivos(CursoCurricula cursoCurricula, DataSessionPivot ds);

    void trasladarToObligatorios(CursoCurricula cursoCurricula, DataSessionPivot ds);

    PlanCurricular findPlanCurricularById(PlanCurricular planCurricular);

    List<CicloAcademico> allUltimosCiclos(Integer cantidadCiclos);

    List<PlanCurricular> allByDynatable(DynatableFilter filter, List<Carrera> carreras);

    List<TipoCursoCurricula> allTiposCursoCurricula();

    List<TipoCursoCurricula> allTiposCursoCurriculasElectivos();

    List<TipoCursoCurricula> allTiposCursoCurriculasObligatorios();

    List<CursoCurricula> allCursosCurriculaByFilter(TipoCursoCurricula tipoCursoCurricula);

    List<Curso> allCursoByNombreTipoCurricula(String nombre, List<TipoCurriculaEnum> tiposCurriculaEnum);

    TipoCursoCurricula findTipoCurricula(Long tipoCursoCurricula);

    List<CursoCurricula> allCursosOblByDynatable(DynatableFilter filter);

    List<CursoCurricula> allCursoCurriculaByNombre(CursoCurricula cursoCurricula);

    List<RequisitoCursoOpcional> allCursosObligatoriosAndElectivosByNombre(CursoCurricula cursoCurricula);

    CursoCurricula findCursoCurricula(Long cursoCurricula);

    CursoOpcionalCurricula findCursoElectivo(Long cursoElectivoId);

    void updateCursoCurricula(CursoCurricula cursoCurricula, DataSessionPivot ds);

    void saveCursoAdicional(CursoAdicionalCurricula cursoAdicionalCurricula, DataSessionPivot ds);

    List<CursoAdicionalCurricula> allCursosAdcByDynatable(DynatableFilter filter);

    void deleteCursoAdicional(Long cursoAdicionalId);

    void saveCursoOpcional(CursoOpcionalCurricula cursoOpcionalCurricula, DataSessionPivot ds);

    void updateCursoOpcional(CursoOpcionalCurricula cursoOpcional, DataSessionPivot ds);

    List<CursoOpcionalCurricula> allCursosElecByDynatable(DynatableFilter filter);

    void deleteCursoOpcional(CursoOpcionalCurricula cursoElectivo);

    List<Curso> allCursosByCodigo(String codigo);

    List<ResumenPlanCurricular> allResPlanCurByDynatable(DynatableFilter filter);

    void updatePlanCurricular(PlanCurricular planCurricular);

    void deletePlanCurricular(PlanCurricular plan);

    void desactivarPlanCurricular(PlanCurricular plan);

    PlanCurricular clonarPlanCurricular(PlanCurricular plan, CicloAcademico ciclo, DataSessionPivot ds);

    void moveCurso(CursoCurricula cursoCurricula, String direccion, DataSessionPivot ds);

    void procesarAlumnos(PlanCurricular plan, CicloAcademico cicloAcademico);

}
