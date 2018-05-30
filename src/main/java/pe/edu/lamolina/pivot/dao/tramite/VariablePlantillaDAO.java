package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.model.tramite.VariablePlantilla;

public interface VariablePlantillaDAO extends EasyDAO<VariablePlantilla> {

    public List<VariablePlantilla> allByPlantilla(PlantillaDocumentoAcademico plantillaDoc);

}
