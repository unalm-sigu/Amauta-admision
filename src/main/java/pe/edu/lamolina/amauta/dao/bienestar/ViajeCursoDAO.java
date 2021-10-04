package pe.edu.lamolina.amauta.dao.bienestar;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.bienestar.ViajeCurso;

public interface ViajeCursoDAO extends EasyDAO<ViajeCurso> {

    List<ViajeCurso> allByDocenteCiclo(Docente docente, CicloAcademico ciclo, DynatableFilter filter);

}
