package pe.edu.lamolina.amauta.dao.horario;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;

public interface TipoGrupoHorasDAO extends EasyDAO<TipoGrupoHoras> {

    List<TipoGrupoHoras> allByDynatable(DynatableFilter filter);

    TipoGrupoHoras findByCode(String codigo);

    TipoGrupoHoras findByTipo(TipoGrupoHorasEnum tipoGrupoHorasEnum);

    TipoGrupoHoras findByTipoCiclo(TipoGrupoHorasEnum tipoGrupoHorasEnum, CicloAcademico cicloAcademico);

    List<TipoGrupoHoras> allActiveByTipoCiclo(CicloAcademico cicloAcademico, TipoGrupoHorasEnum tipoGrupoHorasEnum);

}
