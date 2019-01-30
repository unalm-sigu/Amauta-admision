package pe.edu.lamolina.pivot.controller.consejeria.aconsejados;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AconsejadoService {

    public List<AlumnoConsejero> allAconsejadoByDynatableCarrera(DynatableFilter filter);

    public void updateAlumnoConsejero(AlumnoConsejero alumnoConsejeroForm, DataSessionPivot ds);

}
