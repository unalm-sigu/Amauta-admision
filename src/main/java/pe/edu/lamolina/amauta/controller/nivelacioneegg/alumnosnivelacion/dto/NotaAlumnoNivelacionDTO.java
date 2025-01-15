package pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnosnivelacion.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.zzerializator.DateTimeDeserializer;

@Getter
@Setter
@NoArgsConstructor
public class NotaAlumnoNivelacionDTO {

    private Long id;
    private String estado;
    private BigDecimal puntajeExamen;
    private BigDecimal notaExamen;
    private BigDecimal notaCurso;
    private Boolean temaAprobado;
    private Boolean esMatriculable;
    private String motivo;
    private Curso curso;
    private CursoNivelacion cursoNivelacion;
    private Usuario userRegistro;

    @JsonDeserialize(using = DateTimeDeserializer.class)
    private Date fechaRegistro;

    public NotaAlumnoNivelacionDTO(NotaAlumnoNivelacion previo, String motivo) {
        this.estado = previo.getEstado();
        this.puntajeExamen = previo.getPuntajeExamen();
        this.notaExamen = previo.getNotaExamen();
        this.notaCurso = previo.getNotaCurso();
        this.temaAprobado = previo.getTemaAprobado();
        this.esMatriculable = previo.getEsMatriculable();
        this.curso = previo.getCurso();
        this.cursoNivelacion = previo.getCursoNivelacion();
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
