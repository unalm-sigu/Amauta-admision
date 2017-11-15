package pe.edu.lamolina.pivot.controller.academico.horario.grupo;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.general.Dia;
import pe.edu.lamolina.pivot.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.pivot.model.horario.GrupoHoras;
import pe.edu.lamolina.pivot.model.horario.Hora;

public interface GrupoHorasService {

    List<GrupoHoras> allGrupoHoras(DynatableFilter filter, Long idTipoGrupo);

    GrupoHoras findGrupoHoras(GrupoHoras grupoHoras);

    GrupoHoras findGrupoHoras(Long grupoHoras);

    void delete(GrupoHoras grupoHoras);

    void save(GrupoHoras grupoHoras);

    void update(GrupoHoras grupoHoras);

    GrupoHoras findGrupoHorasByCode(String codigo);

    List<Hora> allHora();

    List<Dia> allDia();

    void saveDiaHoraGrupo(DiaHoraGrupo diaHoraGrupo);

}
