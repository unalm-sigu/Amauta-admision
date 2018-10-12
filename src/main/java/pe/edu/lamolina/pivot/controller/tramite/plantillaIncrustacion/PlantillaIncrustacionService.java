package pe.edu.lamolina.pivot.controller.tramite.plantillaIncrustacion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;

public interface PlantillaIncrustacionService {

    public List<PlantillaDocumentoAcademico> all(DynatableFilter filter);


}
