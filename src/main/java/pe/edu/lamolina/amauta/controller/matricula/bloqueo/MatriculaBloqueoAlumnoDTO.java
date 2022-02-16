package pe.edu.lamolina.amauta.controller.matricula.bloqueo;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.SituacionAcademica;

@Data
@NoArgsConstructor
public class MatriculaBloqueoAlumnoDTO implements Serializable {

    private List<SituacionAcademica> situacionAcademicas;

    private Carrera carrera;

    private CicloAcademico cicloAplica;
    
}
