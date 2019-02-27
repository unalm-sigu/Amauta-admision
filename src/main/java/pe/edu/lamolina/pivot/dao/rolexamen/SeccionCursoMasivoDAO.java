package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;

public interface SeccionCursoMasivoDAO extends EasyDAO<SeccionCursoMasivo> {

    void deleteByCursoMasivo(CursoMasivoExamen cursoMasivoExamen);

    List<SeccionCursoMasivo> allByCursosMasivos(List<CursoMasivoExamen> cursosMasivos);

    List<SeccionCursoMasivo> allSeccionByCursoMasivo(CursoMasivoExamen cursosMasivo);

    List<SeccionCursoMasivo> allByCursosMasivos(List<CursoMasivoExamen> cursosMasivosExamenes, SeccionRolExamenEstadoEnum... estados);

    List<SeccionCursoMasivo> allByCursoMasivo(CursoMasivoExamen cursoMasivo, SeccionRolExamenEstadoEnum... estados);

    void updateEstadoExcluido(SeccionCursoMasivo seccionCursoMasivo);

    void updateEstado(SeccionCursoMasivo cursoMasivoExamen);

    Map<Long, Integer> countByCursosMasivos(List<CursoMasivoExamen> cursosMasivosExamen, SeccionRolExamenEstadoEnum... estados);

    List<SeccionCursoMasivo> allByDynatableAndCursoMasivo(DynatableFilter filter, CursoMasivoExamen cursoMasivoExamen);

    List<SeccionCursoMasivo> allByRolExamenes(RolExamenes rolExamenes, SeccionRolExamenEstadoEnum... estados);

    Integer countDocenteByCursoMasivo(Docente docente, CursoMasivoExamen cursoMasivoExamen, SeccionRolExamenEstadoEnum... estados);

    List<SeccionCursoMasivo> allByDocenteAndEstados(Docente docente, SeccionRolExamenEstadoEnum... estados);
    
    List<SeccionCursoMasivo> allByGrupoHorasExamen(List<GrupoHorasExamen> grupoHorasExamenes);

    SeccionCursoMasivo findByRolExamenesSeccion(RolExamenes rol, Seccion seccion, SeccionRolExamenEstadoEnum... seccionRolExamenEstadoEnum);
}
