package pe.edu.lamolina.pivot.dao.tramite;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.FlujoTramiteDocumento;
import pe.edu.lamolina.model.tramite.Tramite;

public interface FlujoTramiteDocumentoDAO extends EasyDAO<FlujoTramiteDocumento> {

    FlujoTramiteDocumento findByTramite(Tramite tramite);
}
