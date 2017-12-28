package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.horario.GrupoHoras;
import pe.edu.lamolina.pivot.model.horario.TipoGrupoHoras;

public interface GrupoHorasDAO extends Crud<GrupoHoras> {

    GrupoHoras findByCode(String codigo);

    GrupoHoras findGrupoHorasByCode(String codigo);

    List<GrupoHoras> allGrupoHoras(DynatableFilter filter, Long idTipoGrupo);

    GrupoHoras find(GrupoHoras grupoHoras);

    List<GrupoHoras> allByTipoGrupoHora(TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico);

    List<GrupoHoras> allZetasByDynatable(pe.albatross.octavia.dynatable.DynatableFilter filter,
            TipoGrupoHoras tipoGrupoHoras,
            CicloAcademico cicloAcademico);

    List<GrupoHoras> allByTipoGrupoHoraDyna(pe.albatross.octavia.dynatable.DynatableFilter filter,
            TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico);

}
