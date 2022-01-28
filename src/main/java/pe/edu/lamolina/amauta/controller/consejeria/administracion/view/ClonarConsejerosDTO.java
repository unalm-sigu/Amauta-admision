package pe.edu.lamolina.amauta.controller.consejeria.administracion.view;

import javax.validation.constraints.NotNull;
import lombok.Data;
import pe.edu.lamolina.model.academico.CicloAcademico;

@Data
public class ClonarConsejerosDTO {

    @NotNull
    private CicloAcademico modelo;
    @NotNull
    private CicloAcademico destino;
}
