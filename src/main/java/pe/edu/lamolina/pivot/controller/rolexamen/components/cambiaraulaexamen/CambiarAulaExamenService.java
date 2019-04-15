package pe.edu.lamolina.pivot.controller.rolexamen.components.cambiaraulaexamen;

import java.util.List;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.pivot.controller.rolexamen.components.CambiarAula;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface CambiarAulaExamenService {

    SeccionGrupoEspecial findSeccionGrupoEspecialBySeccionRolExamenes(Seccion seccion, RolExamenes rol);

    SeccionGrupoRegular findSeccionGrupoRegularBySeccionRolExamenes(Seccion seccion, RolExamenes rol);

    List<Aula> allActivesAulasOeraForSeccion(Seccion seccion);

    void cambiarAulaExamen(CambiarAula cambiarAula, DataSessionPivot ds);

}
