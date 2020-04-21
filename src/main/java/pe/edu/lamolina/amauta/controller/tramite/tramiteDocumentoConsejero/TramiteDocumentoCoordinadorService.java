package pe.edu.lamolina.amauta.controller.tramite.tramiteDocumentoConsejero;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface TramiteDocumentoCoordinadorService {

    public List<TramiteDocumentoAcademico> allTramiteDocumentoAcademico(DynatableFilter filter, DataSessionPivot ds);

 
}
