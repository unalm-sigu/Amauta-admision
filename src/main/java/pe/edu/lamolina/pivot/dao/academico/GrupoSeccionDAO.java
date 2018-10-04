package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.pivot.controller.academico.acta.ActaResumen;
import pe.edu.lamolina.pivot.controller.academico.gposeccion.GpoSeccionResumen;
import pe.edu.lamolina.pivot.controller.academico.plancalificacurso.DocenteCursoPlan;

public interface GrupoSeccionDAO extends EasyDAO<GrupoSeccion> {

    GrupoSeccion find(Long idGrupoSeccion);

    List<GrupoSeccion> allByFilter(List<Long> ids, CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico, EstadoEnum estadoEnum);

    List<GrupoSeccion> allByDynatableCicloDpto(CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico, DynatableFilter filter);

    List<GrupoSeccion> allByPlan(PlanCalificacion plan);

    GrupoSeccion findByCodeCiclo(String codigo, CicloAcademico ciclo);

    List<GrupoSeccion> allByCiclo(CicloAcademico ciclo);

    List<DocenteCursoPlan> allDocenteCursoPlanByCiclo(CicloAcademico ciclo);

    List<GrupoSeccion> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    GpoSeccionResumen resumenByCiclo(CicloAcademico ciclo);

    GrupoSeccion findLast();

    String findMaxCodigoByCiclo(CicloAcademico cicloAcademico);

    List<GrupoSeccion> allUnusedByCiclo(CicloAcademico ciclo);

    List<String> allCodigoByCiclo(CicloAcademico cicloAcademico);

    List<String> allCodigo2ByCiclo(CicloAcademico cicloAcademico);

    void updateEstadoFechaModUsuarioMod(GrupoSeccion grupoSeccion);

    GrupoSeccion findLock(Long id);

    List<GrupoSeccion> allActivoByCiclo(CicloAcademico cicloAcademico);

    List<GrupoSeccion> allActivoByCicloGrupoNoCerrado(CicloAcademico cicloAcademico);

    ActaResumen findResumenByDepartamento(CicloAcademico ciclo, DepartamentoAcademico dpto);

    List<GrupoSeccion> allByCicloCurso(CicloAcademico ciclo, String codigo, Long curso);

    List<GrupoSeccion> allActivosByDocenteCiclo(Docente docente, CicloAcademico ciclo);

    Map<Long, Long> allCountAlumnos(List<GrupoSeccion> grupos);

    Map<Long, Long> allCountAlumnosWithNf(List<GrupoSeccion> grupos);

    public List<GrupoSeccion> allByDynatableGruposSeccion(DynatableFilter filter, CicloAcademico ciclo, List<GrupoSeccion> gpos);

}
