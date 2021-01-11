package pe.edu.lamolina.amauta.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.PersonaFoto;

public interface PersonaFotoDAO extends EasyDAO<PersonaFoto> {

    List<PersonaFoto> allByTipo(Persona persona, String tipoFoto);

    PersonaFoto findActiva(Persona persona, String tipoFoto);

}
