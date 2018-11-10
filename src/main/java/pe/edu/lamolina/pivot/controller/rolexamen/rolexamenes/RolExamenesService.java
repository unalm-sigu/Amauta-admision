package pe.edu.lamolina.pivot.controller.rolexamen.rolexamenes;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface RolExamenesService {

    List<EventoCicloAcademico> allEventoCicloAcademicos(CicloAcademico cicloAcademico);

    List<RolExamenes> allRolExamenes(DynatableFilter filter, CicloAcademico cicloAcademico);

    void save(RolExamenes rolExamenes, DataSessionPivot ds);

}
