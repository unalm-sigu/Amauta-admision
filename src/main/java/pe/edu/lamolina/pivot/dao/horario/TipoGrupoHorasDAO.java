package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.horario.TipoGrupoHoras;

public interface TipoGrupoHorasDAO extends Crud<TipoGrupoHoras> {

    public List<TipoGrupoHoras> allTipoGrupoHoras(DynatableFilter filter);

}
