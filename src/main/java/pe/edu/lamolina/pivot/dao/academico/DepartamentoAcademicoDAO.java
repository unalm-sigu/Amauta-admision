package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;

public interface DepartamentoAcademicoDAO extends Crud<DepartamentoAcademico> {

    DepartamentoAcademico find(Long id);

    List<DepartamentoAcademico> allActiveByDyna(DynatableFilter filter);

}
