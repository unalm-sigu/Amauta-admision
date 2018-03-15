package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.pivot.controller.academico.acta.ActaResumen;
import pe.edu.lamolina.pivot.controller.academico.gposeccion.GpoSeccionResumen;
import pe.edu.lamolina.pivot.controller.academico.plancalificacurso.DocenteCursoPlan;

public interface GrupoSeccionDAO extends EasyDAO<GrupoSeccion> {

    GrupoSeccion find(Long idGrupoSeccion);

    List<GrupoSeccion> allByFilter(List<Long> ids, CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico, EstadoEnum estadoEnum);

    List<GrupoSeccion> allByFilter(CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico, DynatableFilter filter);

    List<GrupoSeccion> allByPlan(PlanCalificacion plan);

    GrupoSeccion findByCodeCiclo(String codigo, CicloAcademico ciclo);

    List<GrupoSeccion> allByCiclo(CicloAcademico ciclo);

    List<DocenteCursoPlan> allDocenteCursoPlanByCiclo(CicloAcademico ciclo);

    List<GrupoSeccion> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    GpoSeccionResumen resumenByCiclo(CicloAcademico ciclo);

    //GpoSeccionResumen resumen();
    GrupoSeccion findLast();

    void updateEstadoFechaModUsuarioMod(GrupoSeccion grupoSeccion);

    GrupoSeccion findLock(Long id);

    List<GrupoSeccion> allActivoByCiclo(CicloAcademico cicloAcademico);

    List<GrupoSeccion> allActivoByCicloGrupoNoCerrado(CicloAcademico cicloAcademico);

    ActaResumen resumen(CicloAcademico ciclo, DepartamentoAcademico dpto);
}
