package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.pivot.model.horario.GrupoHoras;
import pe.edu.lamolina.pivot.model.horario.TipoGrupoHoras;

public interface DiaHoraGrupoDAO extends Crud<DiaHoraGrupo> {

    public DiaHoraGrupo findByDiaHoraCiclo(DiaHoraGrupo diaHoraGrupo);

    public List<DiaHoraGrupo> allDiaHoraGrupoByGrupo(GrupoHoras grupoHoras, CicloAcademico cicloAcademico);

    public List<DiaHoraGrupo> allDiaHoraGrupo(List<GrupoHoras> grupos);

    public List<DiaHoraGrupo> allDiaHoraGrupoByTipo(TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico);

}
