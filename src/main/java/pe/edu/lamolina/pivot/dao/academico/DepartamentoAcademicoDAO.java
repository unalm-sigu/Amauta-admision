package pe.edu.lamolina.pivot.dao.academico;

import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;

public interface DepartamentoAcademicoDAO extends Crud<DepartamentoAcademico> {

    DepartamentoAcademico find(Long id);

}
