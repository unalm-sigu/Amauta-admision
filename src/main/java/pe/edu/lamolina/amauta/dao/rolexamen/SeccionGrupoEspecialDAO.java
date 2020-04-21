package pe.edu.lamolina.amauta.dao.rolexamen;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.bean.RolExamenDocente;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;

public interface SeccionGrupoEspecialDAO extends EasyDAO<SeccionGrupoEspecial> {

    List<SeccionGrupoEspecial> allByDynatableAndRolExamenes(DynatableFilter filter, RolExamenes rolExamenes, Long incompletos);

    List<SeccionGrupoEspecial> allByRolExamenesAndEstados(RolExamenes rolExamenes, SeccionRolExamenEstadoEnum... estados);

    List<SeccionGrupoEspecial> allByRolExamenes(RolExamenes rolExamenes);

    List<SeccionGrupoEspecial> allBySecciones(RolExamenes rolExamenes, List<Seccion> seccionesForEspecial);

    void deleteByRolExamenes(RolExamenes rolExamenes);

    void updateFechaExamen(SeccionGrupoEspecial SeccionGrupoEspecial);

    void updateEstadoExclusion(SeccionGrupoEspecial seccionGrupoEspecialUpd);

    void updateEstado(SeccionGrupoEspecial seccionGrupoEspecialUpd);

    List<SeccionGrupoEspecial> allByGrupoHorasExamenAndEstados(GrupoHorasExamen grupoHorasExamen, SeccionRolExamenEstadoEnum... estados);

    List<RolExamenDocente> allByDocenteAndCiclo(Docente docente, CicloAcademico cicloAcademico);

    List<SeccionGrupoEspecial> allByRolExamenesForReporte(RolExamenes rol);

    List<SeccionGrupoEspecial> allByGrupoHorasExamen(List<GrupoHorasExamen> grupoHorasExamenes);

    SeccionGrupoEspecial findByRolExamanesSeccion(RolExamenes rol, Seccion seccion, SeccionRolExamenEstadoEnum... estados);

    void updateFechaExamenAndAula(SeccionGrupoEspecial seccionGrupoEspecial);

    int saveList(List<SeccionGrupoEspecial> seccionesEspeciales);

    List<RolExamenDocente> allBySeccionesAndRolExam(RolExamenes rolExam, List<Seccion> listSeccion);

}
