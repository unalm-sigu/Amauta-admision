package pe.edu.lamolina.amauta.controller.nivelacioneegg.confignotanivelacion.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PuntajeMaxMinDTO {

    private BigDecimal minimo;
    private BigDecimal maximo;

    public PuntajeMaxMinDTO(BigDecimal minimo, BigDecimal maximo) {
        this.minimo = minimo;
        this.maximo = maximo;
    }

}
