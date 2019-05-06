package pe.edu.lamolina.pivot.controller.consejeria.aconsejados;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.bean.AconsejadoEstadoBean;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AconsejadoService {

    List<AlumnoConsejero> allAconsejadoByDynatableCarrera(DynatableFilter filter, CicloAcademico cicloAcademico);

    void updateAlumnoConsejero(AlumnoConsejero alumnoConsejeroForm, DataSessionPivot ds);

    AconsejadoEstadoBean allByCarrera(Carrera carrera, CicloAcademico cicloAcademico);

}
