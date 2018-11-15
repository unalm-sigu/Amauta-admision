package pe.edu.lamolina.pivot.controller.academico.departamento;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.general.Compania;

public interface DepartamentoService {

    List<DepartamentoAcademico> allDepartamentoAcademico(DynatableFilter filter, List<DepartamentoAcademico> departamentos);

    DepartamentoAcademico findDepartamentoAcademico(Long idDepartamentoAcademico);

    void save(DepartamentoAcademico departamentoAcademico);

    void update(DepartamentoAcademico departamentoAcademico);

    void delete(DepartamentoAcademico departamentoAcademico);

    void estado(DepartamentoAcademico departamentoAcademico);

    List<DepartamentoCursoDocente> allDepartamentoCursoDocente(List<DepartamentoAcademico> departamentos);

    List<DepartamentoAcademico> allDepartemento(String nombre, Compania compania);

    List<Facultad> allFacultad(String nombre, Compania compania);

    List<Docente> allDocenteByDptoEstado(Long idDpto, String estado);

    List<Curso> allCursoByDptoEstado(Long idDpto, String estado);
}
