package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.rolexamen.RolExamenes;

public interface RolExamenesDAO extends EasyDAO<RolExamenes> {

    List<RolExamenes> allActiveByCiclo(CicloAcademico cicloAcademico);

    List<RolExamenes> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    void updateRolExamenes(RolExamenes rolExamenes);

    void updateSituacion(RolExamenes rolExamenes);

}
