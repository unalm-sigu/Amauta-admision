package pe.edu.lamolina.pivot.dao.tramite;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;

public interface SerieDocumentoDAO extends EasyDAO<SerieDocumento> {

    SerieDocumento findCorrelativo(TipoDocumentoCompania tipo, String nroSerie);

    SerieDocumento findLock(Long id);

}
