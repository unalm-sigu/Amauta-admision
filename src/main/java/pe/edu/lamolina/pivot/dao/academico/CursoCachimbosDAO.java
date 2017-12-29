package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCachimbos;

public interface CursoCachimbosDAO extends EasyDAO<CursoCachimbos> {

    List<CursoCachimbos> allCursoCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico);

    CursoCachimbos findByCursoCiclo(CursoCachimbos cursoCachimbos);

    List<CursoCachimbos> allCursoCachimbos(CicloAcademico cicloAcademico);

    List<CursoCachimbos> allByCarreraCiclo(CicloAcademico cicloAcademico, Carrera carrera);

    List<CursoCachimbos> allByCiclo(CicloAcademico cicloAcademico);

    List<CursoCachimbos> allByCursoCiclo(List<Curso> cursos, CicloAcademico cicloAcademico, Carrera carrera);

}
