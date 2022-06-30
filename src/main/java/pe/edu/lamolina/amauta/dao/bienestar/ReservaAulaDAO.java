package pe.edu.lamolina.amauta.dao.bienestar;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.ReservaAula;

public interface ReservaAulaDAO extends EasyDAO<ReservaAula> {

    List<ReservaAula> allDynatableFilter(DynatableFilter filter);

    ReservaAula find(ReservaAula reservaAula);

}
