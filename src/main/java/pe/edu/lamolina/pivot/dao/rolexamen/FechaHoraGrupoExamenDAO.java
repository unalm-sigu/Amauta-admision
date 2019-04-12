package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.rolexamen.FechaHoraGrupoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;

public interface FechaHoraGrupoExamenDAO extends EasyDAO<FechaHoraGrupoExamen> {

    List<FechaHoraGrupoExamen> allByGrupoHorasExamen(GrupoHorasExamen grupoHorasExamen);

    List<FechaHoraGrupoExamen> allByGrupoHorasExamen(List<GrupoHorasExamen> gruposHorasExamen);

    List<FechaHoraGrupoExamen> allByGrupoHorasExamenOrderByDiaHora(List<GrupoHorasExamen> gruposHorasExamen);

    List<FechaHoraGrupoExamen> allByGrupoHorasExamenOrderByDiaHora(GrupoHorasExamen grupoHorasExamen);

    List<FechaHoraGrupoExamen> allBySemanaExamen(SemanaExamen semanaExamen);

    List<FechaHoraGrupoExamen> allBySemanasExamen(List<SemanaExamen> semanasExamen);

    List<FechaHoraGrupoExamen> allByRolExamens(RolExamenes rolExamenes);

    List<FechaHoraGrupoExamen> allBySemanaExamenAndGrupoHoraSecc(SemanaExamen semanaExamen, List<Long> ids);

}
