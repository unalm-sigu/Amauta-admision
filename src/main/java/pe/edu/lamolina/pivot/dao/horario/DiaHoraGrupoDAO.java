package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.pivot.model.horario.GrupoHoras;

public interface DiaHoraGrupoDAO extends Crud<DiaHoraGrupo> {

    public DiaHoraGrupo findByDiaHoraCiclo(DiaHoraGrupo diaHoraGrupo);

    public List<DiaHoraGrupo> allDiaHoraGrupo(GrupoHoras grupoHoras, CicloAcademico cicloAcademico);

}
