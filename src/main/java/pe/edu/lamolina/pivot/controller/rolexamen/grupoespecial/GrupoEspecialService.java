package pe.edu.lamolina.pivot.controller.rolexamen.grupoespecial;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface GrupoEspecialService {

    List<RolExamenes> allRolExamenesActives(CicloAcademico cicloAcademico);

    RolExamenes findRolExamenes(long rolExamenId);

    List<SeccionGrupoEspecial> allSeccionesGrupoEspecialByRolExamenes(DynatableFilter filter, RolExamenes rolExamenes);

    void deleteGrupoEspecial(RolExamenes rolExamenes);

    void calcularExamenesGrupoEspecial(RolExamenes rolExamenes, DataSessionPivot ds);

    void limpiarExamenGrupoEspecial(RolExamenes rolExamenes, DataSessionPivot ds);

    List<AlumnoGrupoEspecial> allAlumnosGrupoEspecialDynaBySecGpoEsp(DynatableFilter filter, SeccionGrupoEspecial seccionGrupoEspecial);

    void excluirSeccionEspecial(SeccionGrupoEspecial seccionGrupoEspecial, DataSessionPivot ds);

    void excluirAlumnoEspecial(AlumnoGrupoEspecial alumnoGrupoEspecial, DataSessionPivot ds);

    void activarSeccionEspecial(SeccionGrupoEspecial seccionGrupoEspecial, DataSessionPivot ds);

    void activarAlumnoEspecial(AlumnoGrupoEspecial alumnoGrupoEspecial, DataSessionPivot ds);

    void removerAula(SeccionGrupoEspecial grupoSpecial);

    List<GrupoHorasExamen> allGrupoHEForSelect(SeccionGrupoEspecial grupoSpecial);

    void removerGrupo(SeccionGrupoEspecial grupoSpecial);

}
