package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.pivot.controller.academico.departamento.DepartamentoCursoDocente;

public interface DepartamentoAcademicoDAO extends EasyDAO<DepartamentoAcademico> {

    DepartamentoAcademico find(Long id);

    List<DepartamentoAcademico> allActiveByDyna(DynatableFilter filter, List<DepartamentoAcademico> dptos, CicloAcademico cicloAcademico);

    List<DepartamentoAcademico> countByFilter(List<Long> ids, CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico);

    List<DepartamentoAcademico> allByCompania(Compania compania);

    List<DepartamentoAcademico> allDynatable(DynatableFilter filter);

    DepartamentoAcademico findDepartamentoAcademico(Long idDepartamentoAcademico);

    List<DepartamentoCursoDocente> allDepartamentoCursoDocente(List<Long> departamentosList);

    List<DepartamentoAcademico> allDepartemento(String nombre, Compania compania);

    List<DepartamentoAcademico> allDepartamentos(String nombre);

}
