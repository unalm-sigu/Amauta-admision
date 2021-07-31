package pe.edu.lamolina.amauta.controller.tramite.constanciacertificado.descargaWord;

import javax.servlet.http.HttpServletResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.general.Archivo;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;

public interface GeneradorWordSolicitudService {

    void downloadWord(TramiteDocumentoAcademico tramiteDocumentoAcademico, HttpServletResponse response) throws PhobosException;

    public void saveWordTramiteDocumento(Archivo archivo, DataSessionPivot ds);
}
