package pe.edu.lamolina.pivot.controller.academico.departamento;

import java.util.List;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;

public interface DepartamentoService {

    List<DepartamentoAcademico> allDepartamentoAcademico(DynatableFilter filter);

    DepartamentoAcademico findDepartamentoAcademico(Long idDepartamentoAcademico);

    void save(DepartamentoAcademico departamentoAcademico);

    void update(DepartamentoAcademico departamentoAcademico);

    void delete(DepartamentoAcademico departamentoAcademico);

    void estado(DepartamentoAcademico departamentoAcademico);
}
