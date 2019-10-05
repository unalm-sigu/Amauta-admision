package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;

public interface GrupoHorasExamenDAO extends EasyDAO<GrupoHorasExamen> {

    List<GrupoHorasExamen> allByRolExamenes(RolExamenes rolExamenes);

    List<GrupoHorasExamen> allByRolExamenesAndDyna(RolExamenes rolExamenes, DynatableFilter filter);

    GrupoHorasExamen findByRolExamenAndGrupoHoras(RolExamenes rolExamenes, GrupoHoras gruposHora);

    void updateFechaExamen(GrupoHorasExamen grupoHorasExamen);

    void updateVerificado(GrupoHorasExamen grupoHorasExamen);
}
