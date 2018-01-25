package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.horario.HorarioSeccion;

public interface HorarioSeccionDAO extends EasyDAO<HorarioSeccion> {

    List<HorarioSeccion> allBySecciones(List<Seccion> secciones);

    List<HorarioSeccion> allByCicloCurso(CicloAcademico cicloAcademico, List<Curso> cursos);

    List<HorarioSeccion> allBySeccion(Seccion seccion);

}
