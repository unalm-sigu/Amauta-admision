package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionExcluido;

public interface SeccionExcluidoDAO extends EasyDAO<SeccionExcluido> {

    List<SeccionExcluido> allByRolExamenes(RolExamenes rolExamenes);

    void deleteBySecciones(List<Seccion> secciones);

    SeccionExcluido findBySeccion(Seccion seccion, EstadoEnum... estadoEnum);

    void deleteByRolExamenes(RolExamenes rolExamenes);

}
