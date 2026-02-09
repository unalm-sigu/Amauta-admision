package pe.edu.lamolina.amauta.dao.horario;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.horario.PlantillaNivelacion;
import pe.edu.lamolina.model.horario.HorarioCurso;

public interface HorarioCursoDAO extends EasyDAO<HorarioCurso> {

    List<HorarioCurso> allByCursoCicloPlantilla(CursoCicloAcademico cursoCiclo, PlantillaNivelacion plantilla);

    List<HorarioCurso> allByCicloPlantilla(CicloAcademico ciclo, PlantillaNivelacion plantilla);

    List<HorarioCurso> allByCursosCiclo(List<CursoCicloAcademico> cursosCiclo);

    List<HorarioCurso> allByCiclo(CicloAcademico ciclo);
}
