package pe.edu.lamolina.pivot.dao.rolexamen;

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

    List<SeccionGrupoEspecial> allByDynatableAndRolExamenes(DynatableFilter filter, RolExamenes rolExamenes);

    List<SeccionGrupoEspecial> allByRolExamenesAndEstados(RolExamenes rolExamenes, SeccionRolExamenEstadoEnum... estados);

    List<SeccionGrupoEspecial> allByRolExamenes(RolExamenes rolExamenes);

    void deleteByRolExamenes(RolExamenes rolExamenes);

    void updateFechaExamen(SeccionGrupoEspecial SeccionGrupoEspecial);

    void updateEstadoExclusion(SeccionGrupoEspecial seccionGrupoEspecialUpd);

    void updateEstado(SeccionGrupoEspecial seccionGrupoEspecialUpd);

    SeccionGrupoEspecial findBySeccion(Seccion seccion, SeccionRolExamenEstadoEnum... seccionRolExamenEstadoEnum);

    List<SeccionGrupoEspecial> allByGrupoHorasExamenAndEstados(GrupoHorasExamen grupoHorasExamen, SeccionRolExamenEstadoEnum... estados);

    public List<RolExamenDocente> allByDocenteAndCiclo(Docente docente, CicloAcademico cicloAcademico);
}
