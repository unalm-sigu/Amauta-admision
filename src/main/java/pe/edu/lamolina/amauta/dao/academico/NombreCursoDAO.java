package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.NombreCurso;
import pe.edu.lamolina.model.general.Idioma;

public interface NombreCursoDAO extends EasyDAO<NombreCurso> {

    List<NombreCurso> allByCurso(Curso curso);

    public List<NombreCurso> allByIdioma(Idioma idioma);

    public List<NombreCurso> allByCursosIdioma(List<Curso> cursos, Idioma idioma);

}
