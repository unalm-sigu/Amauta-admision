package pe.edu.lamolina.pivot.controller.programacionhorarios.horario.grupo;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;

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

    List<DiaHoraGrupo> allDiaHoraGrupoByGrupo(GrupoHoras grupoHoras, CicloAcademico cicloAcademico);

    void desasignarHora(DiaHoraGrupo diaHoraGrupo);

    List<DiaHoraGrupo> allDiaHoraGrupo(List<GrupoHoras> grupos, CicloAcademico cicloAcademico);

    TipoGrupoHoras findTipoGrupoHoras(Long idTipoGrupo);

    List<DiaHoraGrupo> allDiaHoraGrupoByTipo(TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico);

    void gencolor();

    TipoGrupoHoras findTipoGpoRegular();

    public void clonar(CicloAcademico cicloOrigen, CicloAcademico cicloDestino);

}
