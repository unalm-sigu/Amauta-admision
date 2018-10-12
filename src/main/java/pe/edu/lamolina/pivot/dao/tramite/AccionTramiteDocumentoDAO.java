package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.AccionTramiteDocumento;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;

public interface AccionTramiteDocumentoDAO extends EasyDAO<AccionTramiteDocumento> {

    List<AccionTramiteDocumento> allNextByEstadoInicio(TipoDocumentoAcademico tipoDocumentoAcademico, EstadoTramite estadoTramite);

    public AccionTramiteDocumento findOrderOneByTipoDocumento(TipoDocumentoAcademico tipoDocumentoAcademico, Long order);

}
