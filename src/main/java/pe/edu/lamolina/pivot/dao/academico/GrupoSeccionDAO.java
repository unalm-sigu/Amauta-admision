package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.controller.academico.plancalificacurso.DocenteCursoPlan;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

public interface GrupoSeccionDAO extends Crud<GrupoSeccion> {

    GrupoSeccion find(Long idGrupoSeccion);

    List<GrupoSeccion> allByFilter(List<Long> ids, CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico, EstadoEnum estadoEnum);

    List<GrupoSeccion> allByFilter(CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico, DynatableFilter filter);

    List<GrupoSeccion> allByPlan(PlanCalificacion plan);

    GrupoSeccion findByCodeCiclo(String codigo, CicloAcademico ciclo);

    List<GrupoSeccion> allByCiclo(CicloAcademico ciclo);

    List<DocenteCursoPlan> allDocenteCursoPlanByCiclo(CicloAcademico ciclo);

}
