package pe.edu.lamolina.amauta.dao.escalafon;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.ProduccionEscalafon;

public interface ProduccionEscalafonDAO extends EasyDAO<ProduccionEscalafon> {

    List<ProduccionEscalafon> allByEscalafon(Escalafon escalafon);

}
