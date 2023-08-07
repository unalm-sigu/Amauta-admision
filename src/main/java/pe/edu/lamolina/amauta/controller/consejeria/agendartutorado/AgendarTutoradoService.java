package pe.edu.lamolina.amauta.controller.consejeria.agendartutorado;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tutoria.CitaConsejeroAlumno;

public interface AgendarTutoradoService {

    List<CitaConsejeroAlumno> allByDynatable(DynatableFilter filter, Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds);

    void saveCitaTutorizada(CitaConsejeroAlumno cita, Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds);

    void cancelarCitaTutorado(CitaConsejeroAlumno cita, CicloAcademico ciclo, DataSessionPivot ds);

    void updateCitaTutorado(CitaConsejeroAlumno cita, CicloAcademico ciclo, DataSessionPivot ds);

    void postergarCitaTutorado(CitaConsejeroAlumno cita, CicloAcademico ciclo, DataSessionPivot ds);

    void marcarAsistenciaCita(CitaConsejeroAlumno cita, CicloAcademico ciclo, DataSessionPivot ds);

}
