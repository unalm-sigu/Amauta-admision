package pe.edu.lamolina.pivot.controller.rolexamen.gruporegular;

import java.util.List;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface GrupoRegularService {

    List<RolExamenes> allRolExamenesActives(CicloAcademico cicloAcademico);

    void calcularExamenesGrupoRegular(RolExamenes rolExamenes, CicloAcademico cicloAcademico, DataSessionPivot ds);

}
