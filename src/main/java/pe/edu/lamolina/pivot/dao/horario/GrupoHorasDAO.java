package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;

public interface GrupoHorasDAO extends EasyDAO<GrupoHoras> {

    GrupoHoras findByCode(String codigo);

    GrupoHoras findByCodeTipoCiclo(String codigo, TipoCicloEnum tipoCicloEnum);

    GrupoHoras findGrupoHorasByCode(String codigo);

    List<GrupoHoras> allGrupoHoras(DynatableFilter filter, Long idTipoGrupo);

    GrupoHoras find(GrupoHoras grupoHoras);

    List<GrupoHoras> allByTipoGrupoHora(TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico);

    List<GrupoHoras> allZetasByDynatable(pe.albatross.octavia.dynatable.DynatableFilter filter);

    List<GrupoHoras> allByTipoGpoDynatable(pe.albatross.octavia.dynatable.DynatableFilter filter,
            TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico,
            List<GrupoHoras> grupoHorasFilter);
    
    List<GrupoHoras> allGrupo();
    
    List<GrupoHoras> searchByNombreFilter(String nombre, Integer limit);

}
