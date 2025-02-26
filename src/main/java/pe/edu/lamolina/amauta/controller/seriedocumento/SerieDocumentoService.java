package pe.edu.lamolina.amauta.controller.seriedocumento;

import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface SerieDocumentoService {

    SerieDocumento getCorrelativo(TipoDocumentoCompania tipo, Long nroSerie, Usuario usuario);
//    SerieDocumento getCorrelativoConstanciaCertificado(TipoDocumentoCompania tipo, Long nroSerie, Usuario usuario, Oficina oficina);

}
