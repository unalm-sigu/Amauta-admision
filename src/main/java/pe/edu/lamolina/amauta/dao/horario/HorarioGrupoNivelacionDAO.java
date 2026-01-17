package pe.edu.lamolina.amauta.dao.horario;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.horario.GrupoNivelacion;
import pe.edu.lamolina.model.horario.HorarioGrupoNivelacion;

public interface HorarioGrupoNivelacionDAO extends EasyDAO<HorarioGrupoNivelacion> {

    List<HorarioGrupoNivelacion> allByGrupo(GrupoNivelacion grupo);

    List<HorarioGrupoNivelacion> allByGrupoCiclo(GrupoNivelacion grupo, CicloAcademico ciclo);

    List<HorarioGrupoNivelacion> allByGruposCiclo(List<GrupoNivelacion> grupos, CicloAcademico ciclo);

    List<HorarioGrupoNivelacion> allRegularByCiclo(CicloAcademico ciclo);

    List<HorarioGrupoNivelacion> allByCiclo(CicloAcademico ciclo);
}