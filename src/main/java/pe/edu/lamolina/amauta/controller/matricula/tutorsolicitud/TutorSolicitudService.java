package pe.edu.lamolina.amauta.controller.matricula.tutorsolicitud;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.TutorSolicitud;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface TutorSolicitudService {

    List<TutorSolicitud> allTutorSolicitudByFilter(DynatableFilter filter, CicloAcademico ciclo);

    void updateEstado(Long idAlumnoConsejero, String estado, Usuario usuario);

    void solicitudBeneficio(AlumnoConsejero alumnoConsejero, DataSessionPivot ds);

}
