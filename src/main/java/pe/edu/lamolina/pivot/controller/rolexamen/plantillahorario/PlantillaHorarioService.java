package pe.edu.lamolina.pivot.controller.rolexamen.plantillahorario;

import java.util.List;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.rolexamen.RolExamenes;

public interface PlantillaHorarioService {

    List<RolExamenes> allRolExamenesActives(CicloAcademico cicloAcademico);

    RolExamenes findRolExamenes(RolExamenes rolExamenes);

}
