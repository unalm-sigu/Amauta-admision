package pe.edu.lamolina.amauta.controller.nivelacioneegg.leccionnivelacion.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Date;
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

    public ControlAsistenciaDTO(Date fecha, ControlAsistenciaEstadoEnum estado) {
        this.fecha = fecha;
        this.estado = estado.name();
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
