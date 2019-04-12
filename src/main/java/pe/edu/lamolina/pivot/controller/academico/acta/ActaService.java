package pe.edu.lamolina.pivot.controller.academico.acta;

import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface ActaService {

    List<DepartamentoAcademico> allActiveDepartamentosAcademicos(DynatableFilter filter, List<DepartamentoAcademico> dptos, CicloAcademico cicloAcademico);

    DepartamentoAcademico findDepartamento(Long idDepartamentoAcad);

    List<GrupoSeccion> allGrupoSeccionByFilter(CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico, EstadoEnum estadoEnum);

    List<GrupoSeccion> allGrupoSeccionByFilterDyna(CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico, DynatableFilter dynatableFilter);

    DocenteSeccion findDocenteSeccionByFilter(Docente docente, Seccion seccion);

    List<DocenteSeccion> allDocenteSeccionByFilter(Docente docente, Seccion seccion);

    List<DocenteSeccion> allDocenteSeccionByGrupo(GrupoSeccion grupoSeccion);

    void reabrirGrupo(GrupoSeccion grupoSeccion, Usuario usuario);

    List<DepartamentoAcademico> countGroupsByFilter(List<Long> departamentos, CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico);

    ActaResumen findResumenByDepartamento(CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico);

    List<GrupoSeccion> allGrupoSeccionByCiclo(CicloAcademico cicloAcademico);

    Map mapCantidadAlumnoByGrupo(List<GrupoSeccion> gpoSecciones);

    Map mapCantidadAlumnoByGrupoNF(List<GrupoSeccion> gpoSecciones);

}
