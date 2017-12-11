package pe.edu.lamolina.pivot.controller.academico.departamento;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.general.Compania;

public interface DepartamentoService {

    List<DepartamentoAcademico> allDepartamentoAcademico(DynatableFilter filter);

    DepartamentoAcademico findDepartamentoAcademico(Long idDepartamentoAcademico);

    void save(DepartamentoAcademico departamentoAcademico);

    void update(DepartamentoAcademico departamentoAcademico);

    void delete(DepartamentoAcademico departamentoAcademico);

    void estado(DepartamentoAcademico departamentoAcademico);

    List<DepartamentoCursoDocente> allDepartamentoCursoDocente(List<DepartamentoAcademico> departamentos);

    public List<DepartamentoAcademico> allDepartemento(String nombre, Compania compania);
}
