package pe.edu.lamolina.pivot.dao.medico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.medico.HistoriaClinica;
import pe.edu.lamolina.model.medico.Paciente;

public interface HistoriaClinicaDAO extends EasyDAO<HistoriaClinica> {

    HistoriaClinica findByPersona(Persona persona);

    HistoriaClinica findByPaciente(Paciente paciente);

    List<HistoriaClinica> allByPersona(List<Persona> personas);

}
