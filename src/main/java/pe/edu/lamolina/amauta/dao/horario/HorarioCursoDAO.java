package pe.edu.lamolina.amauta.dao.horario;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.horario.GrupoHorasNivelacion;
import pe.edu.lamolina.model.horario.HorarioCurso;

public interface HorarioCursoDAO extends EasyDAO<HorarioCurso> {

    List<HorarioCurso> allByCursoCicloHorario(CursoCicloAcademico cursoCiclo, GrupoHorasNivelacion grupoHoras);

    List<HorarioCurso> allByCicloHorario(CicloAcademico ciclo, GrupoHorasNivelacion grupoHoras);
}
