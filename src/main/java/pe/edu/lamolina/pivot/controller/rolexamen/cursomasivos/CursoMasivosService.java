package pe.edu.lamolina.pivot.controller.rolexamen.cursomasivos;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.rolexamen.AlumnoCursoMasivo;
import pe.edu.lamolina.model.rolexamen.CursoExcluido;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.DocenteCursoMasivo;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;
import pe.edu.lamolina.pivot.controller.rolexamen.util.RolExamenesLogger;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface CursoMasivosService {

    List<RolExamenes> allRolExamenesByCicloActivo(CicloAcademico cicloAcademico);

    RolExamenes findRolExamenes(RolExamenes rolExamenes);

    void save(CursoMasivoExamen cursoMasivosExamen, CicloAcademico cicloAcademico, DataSessionPivot ds);

    List<CursoMasivoExamen> listCursosMasivosExamenes(RolExamenes rolExamenes);

    List<Curso> allCursosByCiclo(String nombre, RolExamenes rolExamenes, CicloAcademico cicloAcademico);

    List<Aula> allPabellonesByOficina();

    void eliminarCursoMasivoExamen(CursoMasivoExamen cursoMasivoExamen, DataSessionPivot ds);

    List<Aula> allAulasOERAByModulo(Aula modulo);

    void saveAula(CursoMasivoExamen cursoMasivosExamen, CicloAcademico cicloAcademico, DataSessionPivot ds);

    void excluirCursoMasivo(CursoMasivoExamen cursoMasivoExamen, DataSessionPivot ds);

    void excluirSeccionCursoMasivo(SeccionCursoMasivo seccionCursoMasivo, CursoExcluido cursoExcluido, DataSessionPivot ds);

    CursoMasivoExamen findCursoMasivo(Long idCursoMasivo);

    void saveHorarioExamen(CursoMasivoExamen cursoMasivoExamen, DataSessionPivot ds);

    GrupoHorasExamen revisarGpoHorasExamenCursoMasivo(CursoMasivoExamen cursoMasivoExamen, DataSessionPivot ds);

    void excluirDocenteCursoMasivo(DocenteCursoMasivo docenteCursoMasivo, DataSessionPivot ds);

    RolExamenesLogger activarDocenteCursoMasivo(DocenteCursoMasivo docenteCursoMasivo, DataSessionPivot ds);

    void excluirAlumnoCursoMasivo(AlumnoCursoMasivo alumnoCursoMasivo, DataSessionPivot ds);

    List<DocenteCursoMasivo> allDocentesCursosMasivosDynaByCursoMasivo(DynatableFilter filter, CursoMasivoExamen cursoMasivoExamen);

    List<AlumnoCursoMasivo> allAlumnosCursoMasivosDynaByCursoMasivo(DynatableFilter filter, CursoMasivoExamen cursoMasivoExamen);

    List<SeccionCursoMasivo> allSeccionesCursoMasivosDynaByCursoMasivo(DynatableFilter filter, CursoMasivoExamen cursoMasivoExamen);

    void activarCursoMasivo(CursoMasivoExamen cursoMasivoExamen, DataSessionPivot ds);

    RolExamenesLogger activarAlumnoCursoMasivo(AlumnoCursoMasivo alumnoCursoMasivo, DataSessionPivot ds);

    void activarSeccionCursoMasivo(SeccionCursoMasivo seccionCursoMasivo, DataSessionPivot ds);

    boolean validateCruceCursosMasivos(CursoMasivoExamen cursoMasivoExamen, List<Alumno> alumnos, List<Docente> docentes, List<Aula> aulas);

    void eliminarCursosMasivos(RolExamenes rolExamenes);

    void deleteCursosMasivos(RolExamenes rolExamenes);

    List<GrupoHorasExamen> allGrupoHoraExamenByRolExamenes(RolExamenes rolExamenes);

    List<String> cambiarAulasGrupoForCursoMasivo(CursoMasivoExamen cursoMasivosExamen, CicloAcademico cicloAcademico, DataSessionPivot ds);

    List<String> cambiarCambioAulasGrupoForzado(CursoMasivoExamen cursoMasivosExamen, CicloAcademico cicloAcademico, DataSessionPivot ds);

    List<Aula> allAulasVerificadasByModulo(Aula modulo);

    void removerHorario(CursoMasivoExamen cursoMasivo, DataSessionPivot ds);
}
