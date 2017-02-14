package pe.edu.lamolina.pivot.controller.academico.acta;

import java.util.List;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;

public interface ActaService {

    List<DepartamentoAcademico> allActiveDepartamentosAcademicos(DynatableFilter filter);

    DepartamentoAcademico findDepartamento(Long idDepartamentoAcad);

}
