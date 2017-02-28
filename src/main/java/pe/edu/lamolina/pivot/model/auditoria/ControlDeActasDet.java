package pe.edu.lamolina.pivot.model.auditoria;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;

@Entity
@Table(name = "aud_control_de_actas_det")
public class ControlDeActasDet implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evaluacion")
    private Evaluacion evaluacion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_alumno")
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evaluacion_superior")
    private Evaluacion evaluacionSuperior;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_seccion")
    private Seccion seccion;

    @NotNull
    @Column(name = "numero")
    private int numeroEvaluacion;

    @NotNull
    @Column(name = "tipo_seccion")
    private String tipoSeccion;

    @NotNull
    @Column(name = "evaluacion_descripcion")
    private String evaluacionDescripcion;

    @NotNull
    @Column(name = "nota")
    private String nota;

    @NotNull
    @Column(name = "valor_numerico")
    private BigDecimal notaNumerica;

    @NotNull
    @Column(name = "fecha_ingreso_nota")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngresoNota;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_ingreso_nota")
    private Usuario usuarioIngresoNota;

    @JoinColumn(name = "id_control_de_actas")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ControlDeActas controlDeActas;

    public ControlDeActasDet() {
    }

    public ControlDeActasDet(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Evaluacion getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(Evaluacion evaluacion) {
        this.evaluacion = evaluacion;
    }

    public Evaluacion getEvaluacionSuperior() {
        return evaluacionSuperior;
    }

    public void setEvaluacionSuperior(Evaluacion evaluacionSuperior) {
        this.evaluacionSuperior = evaluacionSuperior;
    }

    public Seccion getSeccion() {
        return seccion;
    }

    public void setSeccion(Seccion seccion) {
        this.seccion = seccion;
    }

    public String getTipoSeccion() {
        return tipoSeccion;
    }

    public void setTipoSeccion(String tipoSeccion) {
        this.tipoSeccion = tipoSeccion;
    }

    public String getEvaluacionDescripcion() {
        return evaluacionDescripcion;
    }

    public void setEvaluacionDescripcion(String evaluacionDescripcion) {
        this.evaluacionDescripcion = evaluacionDescripcion;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public int getNumeroEvaluacion() {
        return numeroEvaluacion;
    }

    public void setNumeroEvaluacion(int numeroEvaluacion) {
        this.numeroEvaluacion = numeroEvaluacion;
    }

    public BigDecimal getNotaNumerica() {
        return notaNumerica;
    }

    public void setNotaNumerica(BigDecimal notaNumerica) {
        this.notaNumerica = notaNumerica;
    }

    public Date getFechaIngresoNota() {
        return fechaIngresoNota;
    }

    public void setFechaIngresoNota(Date fechaIngresoNota) {
        this.fechaIngresoNota = fechaIngresoNota;
    }

    public Usuario getUsuarioIngresoNota() {
        return usuarioIngresoNota;
    }

    public void setUsuarioIngresoNota(Usuario usuarioIngresoNota) {
        this.usuarioIngresoNota = usuarioIngresoNota;
    }

    public ControlDeActas getControlDeActas() {
        return controlDeActas;
    }

    public void setControlDeActas(ControlDeActas controlDeActas) {
        this.controlDeActas = controlDeActas;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ControlDeActasDet)) {
            return false;
        }
        ControlDeActasDet other = (ControlDeActasDet) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pe.edu.lamolina.pivot.model.auditoria.ControlDeActasDet[ id=" + id + " ]";
    }

}
