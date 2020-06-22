package pe.edu.lamolina.amauta.dao.escalafon;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.InvestigacionEscalafon;

public interface InvestigacionEscalafonDAO extends EasyDAO<InvestigacionEscalafon> {

    List<InvestigacionEscalafon> allByEscalafon(Escalafon escalafon);

}
