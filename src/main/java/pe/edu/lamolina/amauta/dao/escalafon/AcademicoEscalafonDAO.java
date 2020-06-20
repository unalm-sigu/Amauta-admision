package pe.edu.lamolina.amauta.dao.escalafon;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.escalafon.AcademicoEscalafon;
import pe.edu.lamolina.model.escalafon.Escalafon;

public interface AcademicoEscalafonDAO extends EasyDAO<AcademicoEscalafon> {

    List<AcademicoEscalafon> allByEscalafon(Escalafon escalafon);

}
