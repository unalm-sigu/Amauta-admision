package pe.edu.lamolina.pivot.dao.laboratorio;

import java.util.Date;
import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.medico.HistoriaClinica;
import pe.edu.lamolina.model.medico.HistoriaLaboratorio;

public interface HistoriaLaboratorioDAO extends EasyDAO<HistoriaLaboratorio> {

    HistoriaLaboratorio findByHistoriaClinica(HistoriaClinica historiaClinica);

    List<HistoriaLaboratorio> allByHistoriaClinica(List<HistoriaClinica> historialClinicaes);

    List<HistoriaLaboratorio> allByPersonas(List<Persona> personas);

    List<HistoriaLaboratorio> allByPersonaFilterFecha(List<Persona> personas, Date fecha);

}
