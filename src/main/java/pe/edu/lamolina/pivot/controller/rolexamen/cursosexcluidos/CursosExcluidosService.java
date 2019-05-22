package pe.edu.lamolina.pivot.controller.rolexamen.cursosexcluidos;

import java.util.List;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.rolexamen.CursoExcluido;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionExcluido;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface CursosExcluidosService {

    List<CursoExcluido> allCursosExcluidosByRolExamenes(RolExamenes rolExamenes);

    List<RolExamenes> allRolExamenesByCicloActivo(CicloAcademico cicloAcademico);

    void excluirCurso(CursoExcluido cursoExcluido, DataSessionPivot ds);

    void excluirSeccion(SeccionExcluido seccionExcluido, DataSessionPivot ds);

    void anularExclusion(CursoExcluido cursoExcluido, DataSessionPivot ds);

    RolExamenes findRolExamenes(long rolExamenId);

    List<Seccion> allSeccionesByCicloAndNombreLimit(CicloAcademico ciclo, RolExamenes rolExamenes, String nombre);

    List<SeccionExcluido> allSeccionesExcluidas(CursoExcluido cursoExcluido);

    SeccionExcluido updateSeccionExcluidoEstado(SeccionExcluido seccionExcluidoForm, DataSessionPivot ds);

}
