package pe.edu.lamolina.amauta.controller.tramite.suspendidodisciplina;

import lombok.Data;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;

import java.util.List;

@Data
public class SancionDTO {
    private Long id;
    private String motivo;
    private List<CicloAcademicoDTO> cicloAcademico;
    private List<Long> idsCiclos;
    private AlumnoDTO alumno;
    private Long alumnoId;
}
