package pe.edu.lamolina.amauta.dao.medico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.medico.HistoriaAntropometrica;
import pe.edu.lamolina.model.medico.HistoriaClinica;
import pe.edu.lamolina.model.medico.Paciente;
import pe.edu.lamolina.amauta.dao.medico.HistoriaClinicaDAO;

@Repository
public class HistoriaClinicaDAOH extends AbstractEasyDAO<HistoriaClinica> implements HistoriaClinicaDAO {

    public HistoriaClinicaDAOH() {
        super();
        setClazz(HistoriaClinica.class);
    }

    @Override
    public HistoriaClinica findByPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(HistoriaClinica.class, "hc")
                .join("paciente p", "p.persona per")
                .filter("per.id", persona);
        return find(sql);
    }

    @Override
    public HistoriaClinica findByPaciente(Paciente paciente) {
        Octavia sql = Octavia.query()
                .from(HistoriaClinica.class, "hc")
                .join("paciente p", "p.persona per")
                .filter("p.id", paciente);
        return find(sql);
    }

    @Override
    public List<HistoriaClinica> allByPersonas(List<Persona> personas) {
        Octavia sql = Octavia.query()
                .from(HistoriaClinica.class, "hc")
                .join("paciente p", "p.persona per")
                .in("per.id", personas);
        return all(sql);
    }

}
