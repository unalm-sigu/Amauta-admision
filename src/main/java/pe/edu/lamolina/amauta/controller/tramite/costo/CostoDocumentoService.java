package pe.edu.lamolina.amauta.controller.tramite.costo;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.tramite.PrecioDocumento;

public interface CostoDocumentoService {

    PrecioDocumento findById(PrecioDocumento tramiteDocumentoAcademico);

    List<PrecioDocumento> all(DynatableFilter filter);

    void save(PrecioDocumento tramiteDocumentoAcademico, DataSessionPivot ds);

    void update(PrecioDocumento tramiteDocumentoAcademico, DataSessionPivot ds);

    List<Idioma> allIdioma();

}
