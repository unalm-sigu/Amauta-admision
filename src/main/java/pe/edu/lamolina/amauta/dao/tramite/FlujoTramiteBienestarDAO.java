package pe.edu.lamolina.amauta.dao.tramite;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.FlujoTramiteBienestar;
import pe.edu.lamolina.model.tramite.Tramite;

public interface FlujoTramiteBienestarDAO extends EasyDAO<FlujoTramiteBienestar> {

    FlujoTramiteBienestar findByTramite(Tramite tramite);
}
