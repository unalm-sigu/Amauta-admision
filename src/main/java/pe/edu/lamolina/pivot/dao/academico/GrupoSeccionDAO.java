package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import java.util.Map;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;

public interface GrupoSeccionDAO extends Crud<GrupoSeccion> {

    GrupoSeccion find(Long idGrupoSeccion);

    List<GrupoSeccion> allByFilter(List<Long> ids, CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico);

    List<GrupoSeccion> allByFilter(CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico, DynatableFilter filter);

    List<GrupoSeccion> allByPlan(PlanCalificacion plan);

    GrupoSeccion findByCodeCiclo(String codigo, CicloAcademico ciclo);

    List<GrupoSeccion> allByCiclo(CicloAcademico ciclo);

}
