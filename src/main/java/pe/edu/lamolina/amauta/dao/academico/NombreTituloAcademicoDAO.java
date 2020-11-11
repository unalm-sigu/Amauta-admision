package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.NombreTituloAcademico;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.social.TituloAcademico;

public interface NombreTituloAcademicoDAO extends EasyDAO<NombreTituloAcademico> {

    NombreTituloAcademico findByIdioma(TituloAcademico tituloAcademico, Idioma idioma);

    List<NombreTituloAcademico> allByTitulo(TituloAcademico tituloAcademico);

    List<NombreTituloAcademico> allByIdioma(Idioma idioma);

}
