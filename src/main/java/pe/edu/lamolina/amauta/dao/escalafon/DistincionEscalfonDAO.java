package pe.edu.lamolina.amauta.dao.escalafon;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.escalafon.DistincionEscalafon;
import pe.edu.lamolina.model.escalafon.Escalafon;

public interface DistincionEscalfonDAO extends EasyDAO<DistincionEscalafon> {

    public List<DistincionEscalafon> allByEscalafon(Escalafon escalafon);

}
