package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;

public interface SeccionGrupoEspecialDAO extends EasyDAO<SeccionGrupoEspecial> {

    List<SeccionGrupoEspecial> allByDynatableAndRolExamenes(DynatableFilter filter, RolExamenes rolExamenes);

}
