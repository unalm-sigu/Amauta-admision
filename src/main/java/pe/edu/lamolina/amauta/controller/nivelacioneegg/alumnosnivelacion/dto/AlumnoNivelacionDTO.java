package pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnosnivelacion.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.nivelacioneegg.AlumnoNivelacion;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.zzerializator.DateTimeDeserializer;

@Getter
@Setter
@NoArgsConstructor
public class AlumnoNivelacionDTO {

    private Long id;
    private String estado;
    private BigDecimal puntajeFinal;
    private BigDecimal notaFinal;
    private String motivo;
    private Usuario userRegistro;

    @JsonDeserialize(using = DateTimeDeserializer.class)
    private Date fechaRegistro;

    public EstadoMatriculaEnum getEstadoEnum() {
        if (estado == null) {
            return null;
        }
        return EstadoMatriculaEnum.valueOf(estado);
    }

    public AlumnoNivelacionDTO(AlumnoNivelacion previo, String motivo) {
        this.estado = previo.getEstado();
        this.puntajeFinal = previo.getPuntajeFinal();
        this.notaFinal = previo.getNotaFinal();
        this.motivo = motivo;

        if (previo.getFechaModificacion() != null) {
            this.fechaRegistro = previo.getFechaModificacion();
            this.userRegistro = previo.getUserModificacion();

        } else {
            this.fechaRegistro = previo.getFechaRegistro();
            this.userRegistro = previo.getUserRegistro();
        }
    }
}
