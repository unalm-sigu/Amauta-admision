package pe.edu.lamolina.amauta.controller.tramite.incrustacion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;

public interface PlantillaIncrustacionService {

    List<PlantillaDocumentoAcademico> all(DynatableFilter filter);

    List<Idioma> allIdioma();

    void update(PlantillaDocumentoAcademico plantillaDocumentoAcademico, DataSessionPivot ds);

    void save(PlantillaDocumentoAcademico plantillaDocumentoAcademico, DataSessionPivot ds);

    void delete(Long id);

}
