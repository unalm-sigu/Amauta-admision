package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.GrupoHorasExcluido;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;

public interface GrupoHorasExcluidoDAO extends EasyDAO<GrupoHorasExcluido> {

    GrupoHorasExcluido findByGpoCiclo(GrupoHoras gpoBD, CicloAcademico ciclo);

    List<GrupoHorasExcluido> allByTipoGpoCiclo(TipoGrupoHoras tipoGpo, CicloAcademico cicloAcademico);

    List<GrupoHorasExcluido> allByGpoHorasCiclo(List<GrupoHoras> gpos, CicloAcademico cicloAcademico);

}
