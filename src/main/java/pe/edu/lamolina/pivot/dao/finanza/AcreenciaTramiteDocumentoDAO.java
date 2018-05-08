package pe.edu.lamolina.pivot.dao.finanza;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.finanzas.AcreenciaTramiteDocumento;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;

public interface AcreenciaTramiteDocumentoDAO extends EasyDAO<AcreenciaTramiteDocumento> {

    public AcreenciaTramiteDocumento findByTramiteDocumentoAcademico(TramiteDocumentoAcademico tramiteDocumentoAcademico);

}
