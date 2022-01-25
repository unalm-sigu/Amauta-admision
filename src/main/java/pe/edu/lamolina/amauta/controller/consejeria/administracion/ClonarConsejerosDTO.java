package pe.edu.lamolina.amauta.controller.consejeria.administracion;

import lombok.Data;
import pe.edu.lamolina.model.academico.CicloAcademico;

@Data
public class ClonarConsejerosDTO {
    private CicloAcademico modelo;
    private CicloAcademico destino;
}
