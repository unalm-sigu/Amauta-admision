package pe.edu.lamolina.amauta.dao.escalafon;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.ExperienciaAsesor;

public interface ExperienciaAsesorDAO extends EasyDAO<ExperienciaAsesor> {

    List<ExperienciaAsesor> allByEscalafon(Escalafon escalafon);

}
