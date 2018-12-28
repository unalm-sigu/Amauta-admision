package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoCursoMasivo;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;

public interface AlumnoCursoMasivoDAO extends EasyDAO<AlumnoCursoMasivo> {

    List<AlumnoCursoMasivo> allAlumnoByCursoMasivo(CursoMasivoExamen cursoMasivo);

    List<AlumnoCursoMasivo> allByCursoMasivo(CursoMasivoExamen cursoMasivo, AlumnoRolExamenEstadoEnum... estados);

    List<AlumnoCursoMasivo> allAlumnoByRolExamenes(RolExamenes rolExamenes, AlumnoRolExamenEstadoEnum... estados);

    List<AlumnoCursoMasivo> allByCursosMasivos(List<CursoMasivoExamen> cursosMasivoExamenes, AlumnoRolExamenEstadoEnum... estados);

    List<AlumnoCursoMasivo> allByDynatableAndCursoMasivo(DynatableFilter filter, CursoMasivoExamen cursoMasivoExamen);

    List<AlumnoCursoMasivo> allBySeccionCursosMasivos(List<SeccionCursoMasivo> seccionesCursoMasivo, AlumnoRolExamenEstadoEnum... estados);

    List<AlumnoCursoMasivo> allBySeccionCursosMasivos(SeccionCursoMasivo seccionCursoMasivo, AlumnoRolExamenEstadoEnum... estados);

    void updateEstadoExclusion(AlumnoCursoMasivo alumnoCursoMasivo);

    Map<Long, Integer> countByCursosMasivos(List<CursoMasivoExamen> cursosMasivosExamen, AlumnoRolExamenEstadoEnum... estados);

    Map<Long, Integer> countBySeccionCursosMasivos(List<SeccionCursoMasivo> seccionesCursoMasivo, AlumnoRolExamenEstadoEnum... estados);

    void updateEstado(AlumnoCursoMasivo alumnoCursoMasivo);

}
