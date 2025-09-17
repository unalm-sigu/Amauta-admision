package pe.edu.lamolina.amauta.controller.nivelacioneegg.leccionnivelacion.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.lamolina.model.enums.dictadoclases.ControlAsistenciaEstadoEnum;
import pe.edu.lamolina.model.zzerializator.DateDeserializer;

@Getter
@Setter
@NoArgsConstructor
public class ControlAsistenciaDTO {

    @JsonDeserialize(using = DateDeserializer.class)
    private Date fecha;

    private String estado;
    private String diaSemana;

    public ControlAsistenciaDTO(Date fecha, ControlAsistenciaEstadoEnum estado) {
        Locale localeEspañol = new Locale("es", "ES");
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", localeEspañol);

        this.fecha = fecha;
        this.estado = estado.name();
        this.diaSemana = sdf.format(fecha).toLowerCase();
    }

    public ControlAsistenciaEstadoEnum getEstadoEnum() {
        if (estado == null) {
            return null;
        }
        return ControlAsistenciaEstadoEnum.valueOf(estado);
    }

    public void setEstadoEnum(ControlAsistenciaEstadoEnum estado) {
        if (estado == null) {
            return;
        }
        this.estado = estado.name();
    }

}
