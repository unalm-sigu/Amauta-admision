package pe.edu.lamolina.amauta.controller.consejeria.administracion.view;

import lombok.Data;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.consejeria.Consejero;

@Data
public class FiltroReporteAgendaDTO {

    private Carrera carrera;
    private Consejero consejero;
    
}
