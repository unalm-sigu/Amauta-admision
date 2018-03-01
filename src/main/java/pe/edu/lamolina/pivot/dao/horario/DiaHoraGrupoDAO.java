package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;

public interface DiaHoraGrupoDAO extends EasyDAO<DiaHoraGrupo> {

    DiaHoraGrupo findByDiaHoraCiclo(DiaHoraGrupo diaHoraGrupo);

    List<DiaHoraGrupo> allDiaHoraGrupoByGrupo(GrupoHoras grupoHoras, CicloAcademico cicloAcademico);

    List<DiaHoraGrupo> allDiaHoraGrupo(List<GrupoHoras> grupos);

    List<DiaHoraGrupo> allDiaHoraGrupoByTipo(TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico);

    List<DiaHoraGrupo> allDiaHoraGrupo(List<GrupoHoras> grupos, CicloAcademico cicloAcademico);

    DiaHoraGrupo findByCicloAcademicoGrupoHorasDiaHora(CicloAcademico cicloAcademico, GrupoHoras grupo, Dia dia, Hora hora);

    void deleteAllByNotInList(List<DiaHoraGrupo> horarios);

    List<DiaHoraGrupo> allByGrupo(GrupoHoras grupo, CicloAcademico ciclo);

    void deleteAllInList(List<DiaHoraGrupo> diaHoraGrupos);
}
