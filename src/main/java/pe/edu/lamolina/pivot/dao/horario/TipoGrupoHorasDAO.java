package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.horario.TipoGrupoHoras;
import pe.edu.lamolina.pivot.zelper.enums.TipoGrupoHorasEnum;

public interface TipoGrupoHorasDAO extends Crud<TipoGrupoHoras> {

    public List<TipoGrupoHoras> allTipoGrupoHoras(DynatableFilter filter);

    public TipoGrupoHoras findByCode(String codigo);

    TipoGrupoHoras findByTipo(TipoGrupoHorasEnum tipoGrupoHorasEnum);

    TipoGrupoHoras findByTipoCiclo(TipoGrupoHorasEnum tipoGrupoHorasEnum, CicloAcademico cicloAcademico);

    List<TipoGrupoHoras> allActiveByTipoCiclo(CicloAcademico cicloAcademico, TipoGrupoHorasEnum tipoGrupoHorasEnum);
}
