package pe.edu.lamolina.pivot.controller.academico.horario.tipo;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;

public interface TipoGrupoHorasService {

    List<TipoGrupoHoras> allTipoGrupoHoras(DynatableFilter filter);

    void changeEstado(TipoGrupoHoras tipoGrupo);

    void deleteTipoGpo(TipoGrupoHoras tipoGrupo);

    void updateTipoGpo(TipoGrupoHoras tipoGrupo);

    void saveTipogpo(TipoGrupoHoras tipoGrupo);

    TipoGrupoHoras find(TipoGrupoHoras tipoGrupo);

    TipoGrupoHoras findTipoGrupoHorasByCode(String codigo);

}
