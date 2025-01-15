package pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Date;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import pe.edu.lamolina.model.zzerializator.DateDeserializer;

@Getter
@Setter
@NoArgsConstructor
public class PeriodoDTO {

    @JsonDeserialize(using = DateDeserializer.class)
    private Date fechaReferencia;
    @JsonDeserialize(using = DateDeserializer.class)
    private Date fechaInicio;
    @JsonDeserialize(using = DateDeserializer.class)
    private Date fechaFin;

    public final void calcular() {
        LocalDate fecha = new LocalDate(fechaReferencia);
        this.fechaInicio = fecha.withDayOfWeek(DateTimeConstants.MONDAY).toDate();
        this.fechaFin = fecha.withDayOfWeek(DateTimeConstants.SUNDAY).toDate();
    }

    public PeriodoDTO(Date fechaReferencia) {
        this.fechaReferencia = fechaReferencia;
        this.calcular();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PeriodoDTO that = (PeriodoDTO) o;
        return Objects.equals(fechaInicio, that.fechaInicio)
                && Objects.equals(fechaFin, that.fechaFin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fechaInicio, fechaFin);
    }

}
