package pe.edu.lamolina.pivot.dao.laboratorio.hibernate;

import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
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
    public List<HistoriaLaboratorio> allByPersonas(List<Persona> personas) {
        Octavia sql = Octavia.query()
                .from(HistoriaLaboratorio.class, "hl")
                .join("historiaClinica hc", "hc.paciente pac", "pac.persona per")
                .in("per.id", personas)
                .filter("fechaMuestra", ">", new DateTime("2019-07-01").toDate());
        return all(sql);
    }

    @Override
    public List<HistoriaLaboratorio> allByPersonaFilterFecha(List<Persona> personas, Date fecha) {

        DateTime dtOrg = new DateTime(fecha);
        DateTime dtPlusOne = dtOrg.plusDays(1);

        Octavia sql = Octavia.query()
                .from(HistoriaLaboratorio.class, "hl")
                .join("historiaClinica hc", "hc.paciente pac", "pac.persona per")
                .filter("hl.fechaRegistro", ">=", dtOrg.toDate())
                .filter("hl.fechaRegistro", "<", dtPlusOne.toDate())
                .in("per.id", personas);
        return all(sql);
    }

    @Override
    public void updateColumns(HistoriaLaboratorio historiaLaboratorio, String... columns) {
        Octavia sql = Octavia.update(HistoriaLaboratorio.class, "se");
        for (String column : columns) {
            sql.set(historiaLaboratorio, column);
        }
        this.update(sql);
    }

}
