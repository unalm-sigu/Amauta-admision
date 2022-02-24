package pe.edu.lamolina.amauta.controller.matricula.nivelacion;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.lamolina.model.academico.CicloAcademico;

@Data
@NoArgsConstructor
public class ClonarNivelacionDTO implements Serializable {

    @NotNull
    private CicloAcademico cicloOrigen;

    @NotNull
    private CicloAcademico cicloDestino;

}
