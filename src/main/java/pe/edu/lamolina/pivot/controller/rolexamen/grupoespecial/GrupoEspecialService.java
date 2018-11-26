package pe.edu.lamolina.pivot.controller.rolexamen.grupoespecial;

import java.util.List;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.rolexamen.RolExamenes;

public interface GrupoEspecialService {

    List<RolExamenes> allRolExamenesActives(CicloAcademico cicloAcademico);

}
