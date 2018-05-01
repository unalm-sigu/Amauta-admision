package pe.edu.lamolina.pivot.dao.tramite;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;

public interface TipoDocumentoCompaniaDAO extends EasyDAO<TipoDocumentoCompania> {

    TipoDocumentoCompania findByCodigo(TipoDocumentoCompaniaEnum codigo);

}
