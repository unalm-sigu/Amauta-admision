package pe.edu.lamolina.pivot.controller.academico.acta;

import java.util.List;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;

public interface ActaService {

    List<DepartamentoAcademico> allActiveDepartamentosAcademicos(DynatableFilter filter);

    DepartamentoAcademico findDepartamento(Long idDepartamentoAcad);

    List<GrupoSeccion> allGrupoSeccionByFilter(CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico);

}
