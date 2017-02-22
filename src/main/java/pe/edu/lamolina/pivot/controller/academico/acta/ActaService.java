package pe.edu.lamolina.pivot.controller.academico.acta;

import java.util.List;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;

public interface ActaService {

    List<DepartamentoAcademico> allActiveDepartamentosAcademicos(DynatableFilter filter);

    DepartamentoAcademico findDepartamento(Long idDepartamentoAcad);

    List<GrupoSeccion> allGrupoSeccionByFilter(CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico);

    List<GrupoSeccion> allGrupoSeccionByFilterDyna(CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico, DynatableFilter dynatableFilter);

    DocenteSeccion findDocenteSeccionByFilter(Docente docente, Seccion seccion);

    List<DocenteSeccion> allDocenteSeccionByGrupo(GrupoSeccion grupoSeccion);

    void reabrirGrupo(GrupoSeccion grupoSeccion, Usuario usuario);

}
