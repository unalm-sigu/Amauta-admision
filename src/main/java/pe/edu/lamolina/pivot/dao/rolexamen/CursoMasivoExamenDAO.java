package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.enums.EstadoCursoMasivoEnum;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;

public interface CursoMasivoExamenDAO extends EasyDAO<CursoMasivoExamen> {

    List<CursoMasivoExamen> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<RolExamenes> allRolExamenesByCicloActivo(CicloAcademico cicloAcademico);

    List<Curso> allCursosByCicloActivo(CicloAcademico cicloAcademico);

    List<CursoMasivoExamen> allByRolExamenes(RolExamenes rolExamenes);

    List<CursoMasivoExamen> allByGrupoHorasExamen(GrupoHorasExamen grupoHorasExamen, EstadoCursoMasivoEnum... estados);

    CursoMasivoExamen find(Long id);

    void updateEstadoExcluido(CursoMasivoExamen cursoMasivoExamen);

    void updateEstado(CursoMasivoExamen cursoMasivoExamen);

    void updateFechaExamen(CursoMasivoExamen cursoMasivoExamen);

    List<CursoMasivoExamen> allByRolExamenes(RolExamenes rolExamenes, EstadoCursoMasivoEnum... estados);
}
