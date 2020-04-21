package pe.edu.lamolina.amauta.dao.rolexamen;

import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.enums.DocenteRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.DocenteCursoMasivo;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface DocenteCursoMasivoDAO extends EasyDAO<DocenteCursoMasivo> {

    void deleteByCursoMasivo(CursoMasivoExamen cursoMasivoExamen);

    List<DocenteCursoMasivo> allByRolExamenes(RolExamenes rolExamenes, DocenteRolExamenEstadoEnum... estados);

    Map<Long, Integer> countByCursosMasivos(List<CursoMasivoExamen> cursosMasivosExamen, DocenteRolExamenEstadoEnum... estados);

    List<DocenteCursoMasivo> allByCursoMasivo(CursoMasivoExamen cursoMasivoExamen, DocenteRolExamenEstadoEnum... estados);

    void updateEstadoExclusion(DocenteCursoMasivo docenteCursoMasivo);

    List<DocenteCursoMasivo> allByDynatableAndCursoMasivo(DynatableFilter filter, CursoMasivoExamen cursoMasivoExamen);

    List<DocenteCursoMasivo> allByCursosMasivos(List<CursoMasivoExamen> cursosMasivos, DocenteRolExamenEstadoEnum... estados);

    void updateEstado(DocenteCursoMasivo docenteCursoMasivo);

    List<DocenteCursoMasivo> allByDocenteAndCiclo(Docente docente, CicloAcademico cicloAcademico);

    List<DocenteCursoMasivo> allByGrupoHorasExamenAndEstados(GrupoHorasExamen grupoHorasExamen, DocenteRolExamenEstadoEnum... estados);

    List<DocenteCursoMasivo> allByCursoMasivoAndDocenteAndEstados(CursoMasivoExamen cursoMasivoExamen, Docente docente, DocenteRolExamenEstadoEnum... estados);

    void createDocentesCursoMasivo(
            List<DocenteCursoMasivo> docentesCursoMasivo,
            CursoMasivoExamen cursoMasivo,
            Usuario user);

}
