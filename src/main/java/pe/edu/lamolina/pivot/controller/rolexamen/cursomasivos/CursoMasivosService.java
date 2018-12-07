package pe.edu.lamolina.pivot.controller.rolexamen.cursomasivos;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.rolexamen.AlumnoCursoMasivo;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.DocenteCursoMasivo;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface CursoMasivosService {

    List<RolExamenes> allRolExamenesByCicloActivo(CicloAcademico cicloAcademico);

    void save(CursoMasivoExamen cursoMasivosExamen, CicloAcademico cicloAcademico, DataSessionPivot ds);

    List<CursoMasivoExamen> listCursosMasivosExamenes(RolExamenes rolExamenes);

    List<Curso> allCursosByCiclo(String nombre, RolExamenes rolExamenes, CicloAcademico cicloAcademico);

    Oficina findOficinaOera();

    List<Aula> allPabellonesByOficina(Oficina oficinaOERA);

    void eliminarCursoMasivoExamen(CursoMasivoExamen cursoMasivoExamen, DataSessionPivot ds);

    List<Aula> allAulasByOficinaModulo(Oficina oficinaOERA, Aula modulo);

    void saveAula(CursoMasivoExamen cursoMasivosExamen, CicloAcademico cicloAcademico, DataSessionPivot ds);

    void excluirCursoMasivo(CursoMasivoExamen cursoMasivoExamen, DataSessionPivot ds);

    void excluirSeccionCursoMasivo(SeccionCursoMasivo seccionCursoMasivo, DataSessionPivot ds);

    CursoMasivoExamen findCursoMasivo(Long idCursoMasivo);

    void saveHorarioExamen(CursoMasivoExamen cursoMasivoExamen, DataSessionPivot ds);

    void excluirDocenteCursoMasivo(DocenteCursoMasivo docenteCursoMasivo, DataSessionPivot ds);

    void excluirAlumnoCursoMasivo(AlumnoCursoMasivo alumnoCursoMasivo, DataSessionPivot ds);

    List<DocenteCursoMasivo> allDocentesCursosMasivosDynaByCursoMasivo(DynatableFilter filter, CursoMasivoExamen cursoMasivoExamen);

    List<AlumnoCursoMasivo> allAlumnosCursoMasivosDynaByCursoMasivo(DynatableFilter filter, CursoMasivoExamen cursoMasivoExamen);

    List<SeccionCursoMasivo> allSeccionesCursoMasivosDynaByCursoMasivo(DynatableFilter filter, CursoMasivoExamen cursoMasivoExamen);
}
