package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.horario.HorarioCachimbos;
import pe.edu.lamolina.pivot.model.horario.SeccionHorarioCachimbos;

public interface SeccionHorarioCachimbosDAO extends EasyDAO<SeccionHorarioCachimbos> {

    List<SeccionHorarioCachimbos> allByCursoHora(Carrera carrera, List<Curso> cursos, CicloAcademico cicloAcademico);

    List<SeccionHorarioCachimbos> allByHorario(HorarioCachimbos horario);

    List<SeccionHorarioCachimbos> allByHorarios(List<HorarioCachimbos> horarios);

    List<SeccionHorarioCachimbos> allByCursoCiclo(CicloAcademico cicloAcademico, List<Curso> cursos);

    List<SeccionHorarioCachimbos> allBySeccions(CicloAcademico cicloAcademico, List<Seccion> secciones);

}
