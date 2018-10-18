package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.model.tramite.PlantillaIncrustacionDocumento;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;

public interface PlantillaIncrustacionDAO extends EasyDAO<PlantillaIncrustacionDocumento> {

    List<PlantillaIncrustacionDocumento> allIncrustacionesByTramite(TramiteDocumentoAcademico documentoAcademico);

    public PlantillaIncrustacionDocumento findTramiteAndPlantilla(TramiteDocumentoAcademico tramiteDocumentoAcademico, PlantillaDocumentoAcademico plantillaDocumentoAcademico);
}
