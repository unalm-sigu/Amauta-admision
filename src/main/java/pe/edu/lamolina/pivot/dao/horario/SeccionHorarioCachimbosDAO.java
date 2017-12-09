package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.horario.HorarioCachimbos;
import pe.edu.lamolina.pivot.model.horario.SeccionHorarioCachimbos;

public interface SeccionHorarioCachimbosDAO extends EasyDAO<SeccionHorarioCachimbos> {

    public List<SeccionHorarioCachimbos> allByCursoHora(Carrera carrera, List<Curso> cursos, CicloAcademico cicloAcademico);

    public List<SeccionHorarioCachimbos> allByHorario(HorarioCachimbos horario);

}
