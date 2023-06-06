package pe.edu.lamolina.amauta.controller.consejeria.aconsejadostutor.view;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.lamolina.model.examen.PreguntaExamen;

@Getter
@Setter
@NoArgsConstructor
public class ResumenEncuestaTutoria {

    private Date desde;
    private Date hasta;
    private Integer encuestados;
    private PreguntaExamen pregunta;
    private Map<String, BigDecimal> puntajes;

}
