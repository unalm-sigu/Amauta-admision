package pe.edu.lamolina.amauta.dao.academico;

import java.util.ArrayList;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.SilaboCurso;

public interface SilaboCursoDAO extends EasyDAO<SilaboCurso> {

    List<SilaboCurso> allByDynatable(DynatableFilter filter);

    public List<SilaboCurso> allByIds(ArrayList<Long> silabus);

    public List<SilaboCurso> all();

    public List<SilaboCurso> allByCursoCiclo(Curso curso, CicloAcademico cicloAcademico);

}
