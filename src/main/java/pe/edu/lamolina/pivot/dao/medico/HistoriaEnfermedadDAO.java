package pe.edu.lamolina.pivot.dao.medico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.medico.HistoriaClinica;
import pe.edu.lamolina.model.medico.HistoriaEnfermedad;

public interface HistoriaEnfermedadDAO extends EasyDAO<HistoriaEnfermedad> {

    HistoriaEnfermedad findByHistoriaEnfermedad(HistoriaEnfermedad he);

    List<HistoriaEnfermedad> allByHistoriaClinica(HistoriaClinica historiaClinica);

    List<HistoriaEnfermedad> allRiesgoByHistoriasClinicas(List<HistoriaClinica> historiasClinicas);

}
