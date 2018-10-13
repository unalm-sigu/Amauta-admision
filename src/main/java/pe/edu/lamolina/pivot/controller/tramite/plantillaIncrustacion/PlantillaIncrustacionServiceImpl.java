package pe.edu.lamolina.pivot.controller.tramite.plantillaIncrustacion;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.pivot.dao.tramite.PlantillaDocumentoAcademicoDAO;

@Service
@Transactional(readOnly = true)
public class PlantillaIncrustacionServiceImpl implements PlantillaIncrustacionService {

    @Autowired
    PlantillaDocumentoAcademicoDAO documentoAcademicoDAO;
    
    @Override
    public List<PlantillaDocumentoAcademico> all(DynatableFilter filter) {
        return documentoAcademicoDAO.allDynatableIncrustacion(filter);
    }

}
