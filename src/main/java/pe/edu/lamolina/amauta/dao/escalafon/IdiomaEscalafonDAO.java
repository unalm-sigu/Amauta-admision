package pe.edu.lamolina.amauta.dao.escalafon;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.IdiomaEscalafon;

public interface IdiomaEscalafonDAO extends EasyDAO<IdiomaEscalafon> {

    List<IdiomaEscalafon> allByEscalafon(Escalafon escalafon);

}
