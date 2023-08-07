package pe.edu.lamolina.amauta.controller.medico.paciente;

import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.medico.Paciente;

public interface PacienteService {

    Paciente findPaciente(Persona persona, DataSessionPivot ds);

}
