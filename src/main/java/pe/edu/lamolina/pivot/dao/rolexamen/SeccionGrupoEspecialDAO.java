package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;

public interface SeccionGrupoEspecialDAO extends EasyDAO<SeccionGrupoEspecial> {

    List<SeccionGrupoEspecial> allByDynatableAndRolExamenes(DynatableFilter filter, RolExamenes rolExamenes);

    List<SeccionGrupoEspecial> allByRolExamenesAndEstados(RolExamenes rolExamenes, SeccionRolExamenEstadoEnum... estados);

    List<SeccionGrupoEspecial> allByRolExamenes(RolExamenes rolExamenes);

    void deleteByRolExamenes(RolExamenes rolExamenes);

    void updateFechaExamen(SeccionGrupoEspecial SeccionGrupoEspecial);
}
