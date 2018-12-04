package pe.edu.lamolina.pivot.controller.rolexamen.grupoespecial;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface GrupoEspecialService {

    List<RolExamenes> allRolExamenesActives(CicloAcademico cicloAcademico);

    List<SeccionGrupoEspecial> allSeccionesGrupoEspecialByRolExamenes(DynatableFilter filter, RolExamenes rolExamenes);

    void deleteGrupoEspecial(RolExamenes rolExamenes);

    void calcularExamenesGrupoEspecial(RolExamenes rolExamenes, DataSessionPivot ds);

}
