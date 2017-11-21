package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.horario.GrupoHoras;

public interface GrupoHorasDAO extends Crud<GrupoHoras> {

    GrupoHoras findByCode(String codigo);

    GrupoHoras findGrupoHorasByCode(String codigo);

    List<GrupoHoras> allGrupoHoras(DynatableFilter filter, Long idTipoGrupo);

    GrupoHoras find(GrupoHoras grupoHoras);

}
