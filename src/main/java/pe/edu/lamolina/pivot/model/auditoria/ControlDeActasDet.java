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

@Entity
@Table(name = "aud_control_de_actas_det")
public class ControlDeActasDet implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "id_evaluacion")
    private Long idEvaluacion;

    @NotNull
    @Column(name = "id_evaluacion_superior")
    private long idEvaluacionSuperior;

    @NotNull
    @Column(name = "id_seccion")
    private long idSeccion;

    @NotNull
    @Column(name = "numero")
    private int numero;

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
    private BigDecimal valorNumerico;

    @NotNull
    @Column(name = "fecha_ingreso_nota")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngresoNota;

    @NotNull
    @Column(name = "id_user_ingreso_nota")
    private long idUserIngresoNota;

    @JoinColumn(name = "id_control_de_actas", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ControlDeActas idControlDeActas;

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

    public long getIdEvaluacion() {
        return idEvaluacion;
    }

    public void setIdEvaluacion(long idEvaluacion) {
        this.idEvaluacion = idEvaluacion;
    }

    public long getIdEvaluacionSuperior() {
        return idEvaluacionSuperior;
    }

    public void setIdEvaluacionSuperior(long idEvaluacionSuperior) {
        this.idEvaluacionSuperior = idEvaluacionSuperior;
    }

    public long getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(long idSeccion) {
        this.idSeccion = idSeccion;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
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

    public BigDecimal getValorNumerico() {
        return valorNumerico;
    }

    public void setValorNumerico(BigDecimal valorNumerico) {
        this.valorNumerico = valorNumerico;
    }

    public Date getFechaIngresoNota() {
        return fechaIngresoNota;
    }

    public void setFechaIngresoNota(Date fechaIngresoNota) {
        this.fechaIngresoNota = fechaIngresoNota;
    }

    public long getIdUserIngresoNota() {
        return idUserIngresoNota;
    }

    public void setIdUserIngresoNota(long idUserIngresoNota) {
        this.idUserIngresoNota = idUserIngresoNota;
    }

    public ControlDeActas getIdControlDeActas() {
        return idControlDeActas;
    }

    public void setIdControlDeActas(ControlDeActas idControlDeActas) {
        this.idControlDeActas = idControlDeActas;
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
