package pe.edu.lamolina.amauta.controller.nivelacioneegg.carganivelacion.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.zzerializator.DateDeserializer;

@Getter
@Setter
@NoArgsConstructor
public class PeriodoDiaDTO {

    @JsonDeserialize(using = DateDeserializer.class)
    private Date fechaInicio;
    @JsonDeserialize(using = DateDeserializer.class)
    private Date fechaFin;
    @JsonDeserialize(using = DateDeserializer.class)
    private Date fecha;

    private Dia dia;
    private List<Hora> horas;

    public PeriodoDiaDTO(Date fechaInicio, Date fechaFin, Dia dia, Hora hora) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.dia = dia;

        this.horas = new ArrayList();
        this.horas.add(hora);
    }

    public String getKey() {
        String key = new LocalDate(this.fechaInicio).toString("yyyyMMdd") + "-";
        key += new LocalDate(this.fechaFin).toString("yyyyMMdd") + "-";
        key += this.dia.getNumeroDia();
        return key;
    }

}
