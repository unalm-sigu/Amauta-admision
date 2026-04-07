package pe.edu.lamolina.amauta.controller.nivelacioneegg.gruponivelacion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.GrupoNivelacion;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioGrupoNivelacion;

public interface GrupoNivelacionService {

    List<GrupoNivelacion> allByDynatable(DynatableFilter filter,CicloAcademico ciclo, DataSessionPivot ds);

    void saveGrupo(GrupoNivelacion grupo, DataSessionPivot ds);

    void updateGrupo(GrupoNivelacion grupo, CicloAcademico ciclo, DataSessionPivot ds);

    void eliminarGrupo(GrupoNivelacion grupo, DataSessionPivot ds);

    void saveHorarioGrupo(Long grupoId, List<HorarioGrupoNivelacion> horarios, CicloAcademico ciclo, DataSessionPivot ds);

    List<HorarioGrupoNivelacion> getHorarioGrupo(Long grupoId, CicloAcademico ciclo);

    List<HorarioGrupoNivelacion> getHorarioOtrosGrupos(Long grupoId, CicloAcademico ciclo);

    List<Dia> allDias();

    List<Hora> allHoras();

}
