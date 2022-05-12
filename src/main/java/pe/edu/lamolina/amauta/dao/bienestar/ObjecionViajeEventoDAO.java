package pe.edu.lamolina.amauta.dao.bienestar;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.bienestar.ObjecionViajeEvento;
import pe.edu.lamolina.model.bienestar.ViajeCurso;

public interface ObjecionViajeEventoDAO extends EasyDAO<ObjecionViajeEvento> {

    List<ObjecionViajeEvento> allByViaje(ViajeCurso viajeCurso);

}
