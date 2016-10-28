package pe.edu.lamolina.pivot.model.academico;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import pe.albatross.zelpers.miscelanea.TypesUtil;

@Entity
@Table(name = "aca_logger_plan_calificacion")
public class LoggerPlanCalificacion implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "solicitud")
    private String solicitud;

    @Column(name = "respuesta")
    private String respuesta;

    @Column(name = "comentario")
    private String comentario;

    @Column(name = "id_user_registro")
    private Long idUserRegistro;

    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sistema_academico")
    private PlanCalificacion sistemaAcademico;

    public LoggerPlanCalificacion() {
    }

    public LoggerPlanCalificacion(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlanCalificacion getSistemaAcademico() {
        return sistemaAcademico;
    }

    public void setSistemaAcademico(PlanCalificacion sistemaAcademico) {
        this.sistemaAcademico = sistemaAcademico;
    }

    public String getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(String solicitud) {
        this.solicitud = solicitud;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Long getIdUserRegistro() {
        return idUserRegistro;
    }

    public void setIdUserRegistro(Long idUserRegistro) {
        this.idUserRegistro = idUserRegistro;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

}

