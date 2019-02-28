package pe.edu.lamolina.pivot.dao.medico;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.medico.Paciente;

public interface PacienteDAO extends EasyDAO<Paciente> {

    Paciente findByPersona(Persona persona);
    
}
