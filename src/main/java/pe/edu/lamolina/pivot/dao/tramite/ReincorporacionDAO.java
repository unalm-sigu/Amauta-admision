package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.Tramite;

public interface ReincorporacionDAO extends EasyDAO<Reincorporacion> {

    List<Reincorporacion> allByTramite(Tramite tramite);

    void updateEstado(Reincorporacion reincorporacion);

}
