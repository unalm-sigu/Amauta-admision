package pe.edu.lamolina.pivot.dao.bienestar;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.bienestar.ReservaAula;

public interface ReservaAulaDAO extends EasyDAO<ReservaAula> {

    public List<ReservaAula> allDynatableFilter(DynatableFilter filter);

    public ReservaAula find(ReservaAula reservaAula);

}
