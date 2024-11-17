package pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import pe.albatross.zelpers.spring.deserializer.DateTimeDeserializer;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.seguridad.Usuario;

@Getter
@Setter
@NoArgsConstructor
public class CambioCursoNivevalacionDTO {

    private String cambio;
    private String motivo;
    private Usuario userRegistro;

    @JsonDeserialize(using = DateTimeDeserializer.class)
    private Date fechaRegistro;

    public CambioCursoNivevalacionDTO(CursoNivelacion previo, String cambio, String motivo) {
        this.cambio = cambio;
        this.motivo = motivo;

        if (previo.getFechaModificacion() != null) {
            this.fechaRegistro = previo.getFechaModificacion();
            this.userRegistro = previo.getUserModificacion();

        } else {
            this.fechaRegistro = previo.getFechaRegistro();
            this.userRegistro = previo.getUserRegistro();
        }
    }

    public String getFecha() {
        if (this.fechaRegistro == null) {
            return null;
        }
        return new LocalDate(this.fechaRegistro).toString("dd/MM/yyyy");
    }

    public String getHora() {
        if (this.fechaRegistro == null) {
            return null;
        }
        return new DateTime(this.fechaRegistro).toString("hh:mm a");
    }
}
