package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;

public interface CicloAcademicoDAO extends Crud<CicloAcademico> {

    CicloAcademico findActivo();

    List<CicloAcademico> allForChanges(Integer maxResultado);

    CicloAcademico findAnteriorRegular(CicloAcademico ciclo);

}
