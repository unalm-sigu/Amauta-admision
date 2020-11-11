package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.NombreCarrera;
import pe.edu.lamolina.model.academico.NombreCiclo;
import pe.edu.lamolina.model.general.Idioma;

public interface NombreCicloDAO extends EasyDAO<NombreCiclo> {

    public List<NombreCiclo> allByIdioma(Idioma idioma);

}
