package pe.edu.lamolina.pivot.controller.rolexamen.rolexamenes;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.rolexamen.RolExamenes;

public interface RolExamenesService {

    List<RolExamenes> allRolExamenes(DynatableFilter filter, CicloAcademico cicloAcademico);

}
