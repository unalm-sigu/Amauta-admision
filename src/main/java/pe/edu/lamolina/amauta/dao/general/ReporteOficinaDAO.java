package pe.edu.lamolina.amauta.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.AmbitoReporteEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.ReporteOficina;

public interface ReporteOficinaDAO extends EasyDAO<ReporteOficina> {

    List<ReporteOficina> allByOficinaAmbito(Oficina oficina, AmbitoReporteEnum ambitoReporteEnum);
}
