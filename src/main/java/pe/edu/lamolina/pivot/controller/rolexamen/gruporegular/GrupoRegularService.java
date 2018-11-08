package pe.edu.lamolina.pivot.controller.rolexamen.gruporegular;

import java.util.List;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.rolexamen.GrupoRegularExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;

public interface GrupoRegularService {

    List<RolExamenes> allRolExamenesActives(CicloAcademico cicloAcademico);

    void calcularExamenesGrupoRegular(GrupoRegularExamen grupoRegularExamen, CicloAcademico cicloAcademico);

}
