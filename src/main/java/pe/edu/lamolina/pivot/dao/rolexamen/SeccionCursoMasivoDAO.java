package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;

public interface SeccionCursoMasivoDAO extends EasyDAO<SeccionCursoMasivo> {

    List<SeccionCursoMasivo> allByCursosMasivos(List<CursoMasivoExamen> cursosMasivos);

    List<SeccionCursoMasivo> allSeccionByCursoMasivo(CursoMasivoExamen cursosMasivo);

    List<SeccionCursoMasivo> allByCursosMasivos(List<CursoMasivoExamen> cursosMasivosExamenes, SeccionRolExamenEstadoEnum... estados);

    void updateEstadoExcluido(SeccionCursoMasivo seccionCursoMasivo);

    Map<Long, Integer> countByCursosMasivos(List<CursoMasivoExamen> cursosMasivosExamen, SeccionRolExamenEstadoEnum... estados);

    List<SeccionCursoMasivo> allByDynatableAndCursoMasivo(DynatableFilter filter, CursoMasivoExamen cursoMasivoExamen);
}
