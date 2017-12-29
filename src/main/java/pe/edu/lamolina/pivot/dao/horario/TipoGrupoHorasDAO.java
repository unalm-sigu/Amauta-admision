package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;

public interface TipoGrupoHorasDAO extends EasyDAO<TipoGrupoHoras> {

    List<TipoGrupoHoras> allTipoGrupoHoras(DynatableFilter filter);

    TipoGrupoHoras findByCode(String codigo);

}
