package pe.edu.lamolina.pivot.controller.academico.horario.tipo;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.horario.TipoGrupoHoras;

public interface TipoGrupoHorasService {

    List<TipoGrupoHoras> allTipoGrupoHoras(DynatableFilter filter);

    void estado(TipoGrupoHoras tipoGrupo);

    void delete(TipoGrupoHoras tipoGrupo);

    void update(TipoGrupoHoras tipoGrupo);

    void save(TipoGrupoHoras tipoGrupo);

    TipoGrupoHoras find(TipoGrupoHoras tipoGrupo);

}
