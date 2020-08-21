package pe.edu.lamolina.amauta.dao.tramite;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteBachiller;

public interface TramiteBachillerDAO extends EasyDAO<TramiteBachiller> {

    public TramiteBachiller findByTramite(Tramite tramite);

    public List<TramiteBachiller> allByTramites(List<Tramite> tramites);

}
