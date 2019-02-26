package pe.edu.lamolina.pivot.dao.medico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.medico.HistoriaClinica;
import pe.edu.lamolina.model.medico.HistoriaEnfermedad;
import pe.edu.lamolina.pivot.dao.medico.HistoriaEnfermedadDAO;

@Repository
public class HistoriaEnfermedadDAOH extends AbstractEasyDAO<HistoriaEnfermedad> implements HistoriaEnfermedadDAO {

    public HistoriaEnfermedadDAOH() {
        super();
        setClazz(HistoriaEnfermedad.class);
    }

    @Override
    public HistoriaEnfermedad findByHistoriaEnfermedad(HistoriaEnfermedad he) {
        Octavia sql = Octavia.query()
                .from(HistoriaEnfermedad.class, "henf")
                .join("historiaClinica hc", "enfermedad enf")
                .filter("hc.id", he.getHistoriaClinica().getId())
                .filter("enf.id", he.getEnfermedad().getId());
        return find(sql);
    }

    @Override
    public List<HistoriaEnfermedad> allByHistoriaClinica(HistoriaClinica historiaClinica) {
        Octavia sql = Octavia.query()
                .from(HistoriaEnfermedad.class, "henf")
                .join("historiaClinica hc", "enfermedad enf")
                .filter("hc.id", historiaClinica)
                .orderBy("henf.fechaRegistro");
        return all(sql);
    }

}
