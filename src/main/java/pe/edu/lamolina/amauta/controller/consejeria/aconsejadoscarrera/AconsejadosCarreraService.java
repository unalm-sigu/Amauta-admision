package pe.edu.lamolina.amauta.controller.consejeria.aconsejadoscarrera;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.ConsejeriaResumen;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface AconsejadosCarreraService {

    List<AlumnoConsejero> allAconsejadoByDynatable(Carrera carrera, DynatableFilter filter, CicloAcademico cicloAcademico);

    void updateAlumnoConsejero(AlumnoConsejero alumnoConsejeroForm, DataSessionPivot ds);

    ConsejeriaResumen getResumenByCarreraCiclo(Carrera carrera, CicloAcademico cicloAcademico);

}
