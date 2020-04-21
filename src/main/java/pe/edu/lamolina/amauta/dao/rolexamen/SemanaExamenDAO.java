package pe.edu.lamolina.amauta.dao.rolexamen;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;

public interface SemanaExamenDAO extends EasyDAO<SemanaExamen> {

    List<SemanaExamen> allByRolExamenes(RolExamenes rolExamenes);

    void deleteByRolExamenes(RolExamenes rolExamenes);

}
