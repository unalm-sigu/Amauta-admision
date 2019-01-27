package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;

public interface LetraGrupoRegularDAO extends EasyDAO<LetraGrupoRegular> {

    List<LetraGrupoRegular> allByRolExamenes(RolExamenes rolExamenes);

    LetraGrupoRegular findByGrupoHorasExamen(GrupoHorasExamen grupoHorasExamen);

    List<LetraGrupoRegular> allByRolExamenesForReporte(RolExamenes rol);

    void deleteByRolExamenes(RolExamenes rolExamenes);

}
