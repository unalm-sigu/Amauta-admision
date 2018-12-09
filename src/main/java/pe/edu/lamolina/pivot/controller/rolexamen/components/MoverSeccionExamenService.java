package pe.edu.lamolina.pivot.controller.rolexamen.components;

import java.util.List;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface MoverSeccionExamenService {

    SeccionCursoMasivo findSeccionCursoMasivoBySeccion(Seccion seccion);

    SeccionGrupoRegular findSeccionGrupoRegularBySeccion(Seccion seccion);

    SeccionGrupoEspecial findSeccionGrupoEspecialBySeccion(Seccion seccion);

    List<CursoMasivoExamen> allActiveCursosMasivosByRolExamenes(RolExamenes rolExamenes);

    List<LetraGrupoRegular> allLetrasGruposRegularesByRolExamenes(RolExamenes rolExamenes);

    GrupoHorasExamen findGrupoHorasExamen(GrupoHorasExamen grupoHorasExamen);

    void cambioHorarioExamenSeccion(CambioHorarioExamenSeccion cambioHorarioExamenSeccion, DataSessionPivot ds);

}
