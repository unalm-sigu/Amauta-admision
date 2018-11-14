package pe.edu.lamolina.pivot.controller.rolexamen.plantillahorario;

import java.util.List;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;

public interface PlantillaHorarioService {

    List<RolExamenes> allRolExamenesActives(CicloAcademico cicloAcademico);

    RolExamenes findRolExamenes(RolExamenes rolExamenes);

    void calcularPlantillaHorario(SemanaExamen semanaExamen);

    List<GrupoHoras> allGrupoHorasBySemanaExamen(SemanaExamen semanaExamen);

}
