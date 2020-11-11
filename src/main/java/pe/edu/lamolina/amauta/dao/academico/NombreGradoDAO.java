package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.GradoAcademico;
import pe.edu.lamolina.model.academico.NombreGrado;
import pe.edu.lamolina.model.academico.NombreTituloAcademico;
import pe.edu.lamolina.model.general.Idioma;

public interface NombreGradoDAO extends EasyDAO<NombreGrado> {

    NombreGrado findByIdioma(GradoAcademico gradoAcademico, Idioma idioma);

    List<NombreGrado> allByTitulo(GradoAcademico gradoAcademico);

    List<NombreGrado> allByIdioma(Idioma idioma);

}
