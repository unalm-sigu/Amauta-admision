package pe.edu.lamolina.amauta.dao.bienestar;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.bienestar.ProformaEventoSubvencionado;
import pe.edu.lamolina.model.bienestar.ViajeCurso;

public interface ProformaEventoSubvencionadoDAO extends EasyDAO<ProformaEventoSubvencionado> {

    List<ProformaEventoSubvencionado> allByViajeCurso(ViajeCurso viajeCurso);
}
