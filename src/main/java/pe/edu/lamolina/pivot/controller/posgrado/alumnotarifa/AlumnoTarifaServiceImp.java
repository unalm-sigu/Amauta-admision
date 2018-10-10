package pe.edu.lamolina.pivot.controller.posgrado.alumnotarifa;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.posgrado.AlumnoTarifa;
import pe.edu.lamolina.pivot.dao.posgrado.AlumnoTarifaDAO;

@Service
public class AlumnoTarifaServiceImp implements AlumnoTarifaService {
    
    @Autowired
    AlumnoTarifaDAO alumnoTarifaDAO;

    @Override
    public List<AlumnoTarifa> allAlumnoTarifa(DynatableFilter filter) {
        return alumnoTarifaDAO.allDynaTable(filter);
    }

}
