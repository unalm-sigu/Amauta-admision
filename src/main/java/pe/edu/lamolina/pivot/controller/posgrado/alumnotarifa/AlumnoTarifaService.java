package pe.edu.lamolina.pivot.controller.posgrado.alumnotarifa;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.posgrado.AlumnoTarifa;

public interface AlumnoTarifaService {

    List<AlumnoTarifa> allAlumnoTarifa(DynatableFilter filter);
    

}
