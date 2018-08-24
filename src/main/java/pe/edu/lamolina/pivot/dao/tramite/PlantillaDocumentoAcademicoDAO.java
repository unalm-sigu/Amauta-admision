package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;

public interface PlantillaDocumentoAcademicoDAO extends EasyDAO<PlantillaDocumentoAcademico> {

    public List<PlantillaDocumentoAcademico> allDynatable(DynatableFilter filter);

    public PlantillaDocumentoAcademico find(Long id);
    
    public PlantillaDocumentoAcademico find(PlantillaDocumentoAcademico plantillaDocumentoAcademico);

}
