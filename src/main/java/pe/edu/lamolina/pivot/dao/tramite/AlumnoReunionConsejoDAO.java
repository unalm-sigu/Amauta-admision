package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.AlumnoReunionConsejo;
import pe.edu.lamolina.model.tramite.ReunionConsejo;

public interface AlumnoReunionConsejoDAO extends EasyDAO<AlumnoReunionConsejo> {

    public List<AlumnoReunionConsejo> allByReunionConsejo(ReunionConsejo reunionConsejo);
}
