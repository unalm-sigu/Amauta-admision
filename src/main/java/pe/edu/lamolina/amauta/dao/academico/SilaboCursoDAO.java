package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.SilaboCurso;

public interface SilaboCursoDAO extends EasyDAO<SilaboCurso> {

    List<SilaboCurso> allByDynatable(DynatableFilter filter);

    List<SilaboCurso> allParents();

}
