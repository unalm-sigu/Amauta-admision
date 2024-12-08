package pe.edu.lamolina.amauta.controller.nivelacioneegg.leccionnivelacion.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.lamolina.model.zzerializator.DateDeserializer;

@Getter
@Setter
@NoArgsConstructor
public class PeriodoDiaDTO {

    @JsonDeserialize(using = DateDeserializer.class)
    private Date fechaInicio;
    @JsonDeserialize(using = DateDeserializer.class)
    private Date fechaFin;

    private List<Integer> diasSemanas;

    public PeriodoDiaDTO(Date fechaInicio, Date fechaFin, Integer diaSemana) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;

        this.diasSemanas = new ArrayList();
        this.diasSemanas.add(diaSemana);
    }

}
