package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.controller.academico.departamento.DepartamentoCursoDocente;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.general.Compania;

public interface DepartamentoAcademicoDAO extends Crud<DepartamentoAcademico> {

    DepartamentoAcademico find(Long id);

    List<DepartamentoAcademico> allActiveByDyna(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<DepartamentoAcademico> countByFilter(List<Long> ids, CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico);

    List<DepartamentoAcademico> allByCompania(Compania compania);

    public List<DepartamentoAcademico> allDynatable(DynatableFilter filter);

    public DepartamentoAcademico findDepartamentoAcademico(Long idDepartamentoAcademico);

    public List<DepartamentoCursoDocente> allDepartamentoCursoDocente(List<Long> departamentosList);

    public List<DepartamentoAcademico> allDepartemento(String nombre, Compania compania);

}
