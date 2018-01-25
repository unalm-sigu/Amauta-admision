package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.ConvenioBeca;

public interface ConvenioBecaDAO extends EasyDAO<ConvenioBeca> {

    public List<ConvenioBeca> allByDynatable(DynatableFilter filter);

}
