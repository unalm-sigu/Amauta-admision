package pe.edu.lamolina.amauta.dao.academico;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.NombreFacultad;
import pe.edu.lamolina.model.general.Idioma;

public interface NombreFacultadDAO extends EasyDAO<NombreFacultad> {

    public NombreFacultad findByIdioma(Facultad facultadAlumno, Idioma idioma);

}
