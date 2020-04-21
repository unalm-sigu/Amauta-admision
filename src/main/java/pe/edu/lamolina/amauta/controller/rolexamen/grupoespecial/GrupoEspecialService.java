package pe.edu.lamolina.amauta.controller.rolexamen.grupoespecial;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface GrupoEspecialService {

    List<RolExamenes> allRolExamenesActives(CicloAcademico cicloAcademico);

    RolExamenes findRolExamenes(long rolExamenId);

    List<SeccionGrupoEspecial> allSeccionesGrupoEspecialByRolExamenes(DynatableFilter filter, RolExamenes rolExamenes, Long incompletos);

    void deleteGrupoEspecial(RolExamenes rolExamenes);

    void calcularExamenesGrupoEspecial(RolExamenes rolExamenes, DataSessionPivot ds);

    void limpiarExamenGrupoEspecial(RolExamenes rolExamenes, DataSessionPivot ds);

    List<AlumnoGrupoEspecial> allAlumnosGrupoEspecialDynaBySecGpoEsp(DynatableFilter filter, SeccionGrupoEspecial seccionGrupoEspecial);

    void excluirSeccionEspecial(SeccionGrupoEspecial seccionGrupoEspecial, DataSessionPivot ds);

    void excluirAlumnoEspecial(AlumnoGrupoEspecial alumnoGrupoEspecial, DataSessionPivot ds);

    void activarSeccionEspecial(SeccionGrupoEspecial seccionGrupoEspecial, DataSessionPivot ds);

    void activarAlumnoEspecial(AlumnoGrupoEspecial alumnoGrupoEspecial, DataSessionPivot ds);

    void removerAula(SeccionGrupoEspecial grupoSpecial);

    List<GrupoHorasExamen> allGrupoHoraExamenByRolExamenes(RolExamenes rolExamenes);

    void removerGrupo(SeccionGrupoEspecial grupoSpecial);

    List<String> saveCambioAulaGrupo(SeccionGrupoEspecial grupoSpecial);

    List<String> saveCambioAulaNuevoCM(SeccionGrupoEspecial grupoSpecial, CicloAcademico ciclo, Usuario usuario);

    List<String> saveCambioAulaGrupoForzardo(SeccionGrupoEspecial grupoSpecial);

    Seccion findSeccionByRolExamenes(Seccion seccion, CicloAcademico ciclo, RolExamenes rolExamenes);

    void addSeccionNueva(Seccion seccion, CicloAcademico cicloAcademico, RolExamenes rolExamenes, DataSessionPivot ds);

    void crearCursoMasivo(SeccionGrupoEspecial grupoSpecial, Usuario usuario, CicloAcademico ciclo);

    List<String> saveCambioAulaGrupo3(SeccionGrupoEspecial grupoSpecial, CicloAcademico cicloAcademico, Usuario usuario);

}
