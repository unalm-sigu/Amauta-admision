package pe.edu.lamolina.amauta.dao.academico;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.NombreCarrera;
import pe.edu.lamolina.model.general.Idioma;

public interface NombreCarreraDAO extends EasyDAO<NombreCarrera> {

    public NombreCarrera findByIdioma(Carrera carrera, Idioma idioma);

}
