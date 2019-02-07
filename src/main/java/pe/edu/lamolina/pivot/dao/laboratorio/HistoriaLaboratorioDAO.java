package pe.edu.lamolina.pivot.dao.laboratorio;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.medico.HistoriaLaboratorio;

public interface HistoriaLaboratorioDAO extends EasyDAO<HistoriaLaboratorio> {

    HistoriaLaboratorio findByRecorridoIngresante(RecorridoIngresante recorrido);
}
