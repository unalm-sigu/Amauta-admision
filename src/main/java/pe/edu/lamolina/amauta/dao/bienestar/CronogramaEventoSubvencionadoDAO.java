package pe.edu.lamolina.amauta.dao.bienestar;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.bienestar.CronogramaEventoSubvencionado;
import pe.edu.lamolina.model.bienestar.ViajeCurso;

public interface CronogramaEventoSubvencionadoDAO extends EasyDAO<CronogramaEventoSubvencionado> {

    List<CronogramaEventoSubvencionado> allByViajeCurso(ViajeCurso viajeCurso);
}
