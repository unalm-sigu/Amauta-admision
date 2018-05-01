package pe.edu.lamolina.pivot.controller.seriedocumento;

import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface SerieDocumentoService {

    SerieDocumento getCorrelativo(TipoDocumentoCompania tipo, Long nroSerie, Usuario usuario);

}
