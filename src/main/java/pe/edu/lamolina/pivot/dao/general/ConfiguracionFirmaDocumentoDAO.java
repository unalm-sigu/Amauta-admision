package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.ConfiguracionFirmaDocumento;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;

public interface ConfiguracionFirmaDocumentoDAO extends EasyDAO<ConfiguracionFirmaDocumento> {

    public List<ConfiguracionFirmaDocumento> allByTipoDocumentoAcademico(TipoDocumentoAcademico tipoDocumentoAcademico);

    public void deleteByTipoDocumentoAcademicos(TipoDocumentoAcademico tipoDocumentoAcademico);

}
