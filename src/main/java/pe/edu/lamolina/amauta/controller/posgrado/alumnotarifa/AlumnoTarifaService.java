package pe.edu.lamolina.amauta.controller.posgrado.alumnotarifa;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.posgrado.AlumnoTarifa;
import pe.edu.lamolina.model.posgrado.TarifaCarrera;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface AlumnoTarifaService {

    List<AlumnoTarifa> allAlumnoTarifa(DynatableFilter filter);
    
    void save(AlumnoTarifa alumnotarifa, DataSessionPivot ds);

    public List<TarifaCarrera> allOtrasTarifas(Alumno alumno);

}
