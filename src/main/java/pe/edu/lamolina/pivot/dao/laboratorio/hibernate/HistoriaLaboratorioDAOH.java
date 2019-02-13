package pe.edu.lamolina.pivot.dao.laboratorio.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.medico.HistoriaClinica;
import pe.edu.lamolina.model.medico.HistoriaLaboratorio;
import pe.edu.lamolina.pivot.dao.laboratorio.HistoriaLaboratorioDAO;

@Repository
public class HistoriaLaboratorioDAOH extends AbstractEasyDAO<HistoriaLaboratorio> implements HistoriaLaboratorioDAO {

    public HistoriaLaboratorioDAOH() {
        super();
        setClazz(HistoriaLaboratorio.class);
    }

    @Override
    public HistoriaLaboratorio findByHistoriaClinica(HistoriaClinica historiaClinica) {
        Octavia sql = Octavia.query()
                .from(HistoriaLaboratorio.class, "hl")
                .join("historiaClinica hc")
                .filter("hc.id", historiaClinica);
        return find(sql);
    }

    @Override
    public List<HistoriaLaboratorio> allByHistoriaClinica(List<HistoriaClinica> historialClinicaes) {
        Octavia sql = Octavia.query()
                .from(HistoriaLaboratorio.class, "hl")
                .join("historiaClinica hc")
                .in("hc.id", historialClinicaes);
        return all(sql);
    }
    
    @Override
    public List<HistoriaLaboratorio> allByPersona(List<Persona> personas) {
        Octavia sql = Octavia.query()
                .from(HistoriaLaboratorio.class, "hl")
                .join("historiaClinica hc", "hc.paciente pac", "pac.persona per")
                .in("per.id", personas);
        return all(sql);
    }

}
