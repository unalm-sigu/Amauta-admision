package pe.edu.lamolina.pivot.dao.laboratorio.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.medico.HistoriaLaboratorio;
import pe.edu.lamolina.pivot.dao.laboratorio.HistoriaLaboratorioDAO;

@Repository
public class HistoriaLaboratorioDAOH extends AbstractEasyDAO<HistoriaLaboratorio> implements HistoriaLaboratorioDAO {

    public HistoriaLaboratorioDAOH() {
        super();
        setClazz(HistoriaLaboratorio.class);
    }

    @Override
    public HistoriaLaboratorio findByRecorridoIngresante(RecorridoIngresante recorrido) {
        Octavia sql = Octavia.query()
                .from(HistoriaLaboratorio.class, "hl")
                .join("recorridoIngresante ri")
                .filter("ri.id", recorrido);
        return find(sql);
    }

}
