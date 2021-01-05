package pe.edu.lamolina.amauta.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.PersonaCuentaBancaria;

public interface PersonaCuentaBancariaDAO extends EasyDAO<PersonaCuentaBancaria> {

    List<PersonaCuentaBancaria> allByPersona(Persona persona);

    PersonaCuentaBancaria findActivo(Persona persona);

}
