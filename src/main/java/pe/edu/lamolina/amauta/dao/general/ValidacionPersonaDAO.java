package pe.edu.lamolina.amauta.dao.general;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.ValidacionPersona;

public interface ValidacionPersonaDAO extends EasyDAO<ValidacionPersona> {

    ValidacionPersona findAnterior(Persona persona);

}
