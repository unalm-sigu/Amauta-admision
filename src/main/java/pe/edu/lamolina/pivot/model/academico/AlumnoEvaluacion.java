package pe.edu.lamolina.pivot.model.academico;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.UniqueConstraint;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.enums.MotivoAnulacionEnum;

@Entity
@Table(name = "aca_alumno_evaluacion", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_alumno", "id_evaluacion"})
})
public class AlumnoEvaluacion implements Serializable {

    public final static String NSP = "NSP";

    public final static String NCV = "NCV";

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "estado")
    private String estado;

    @Column(name = "nota")
    private String nota;

    @Column(name = "valor_numerico")
    private BigDecimal valorNumerico;

    @Column(name = "es_ingreso_regular")
    private Integer esIngresoRegular;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_ingreso_nota")
    private Usuario usuarioIngresoNota;

    @Column(name = "fecha_ingreso_nota")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaIngresoNota;

    @Column(name = "id_reclamo_nota")
    private Long idReclamoNota;

    @Column(name = "id_user_anulacion")
    private Long idUserAnulacion;

    @Column(name = "fecha_anulacion")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaAnulacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evaluacion")
    private Evaluacion evaluacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_alumno")
    private Alumno alumno;
    /*
    @Column(name = "ind_nota_anulada")
    private Integer indNotaAnulada;
     */
    @Column(name = "motivo_anulacion")
    private String motivoAnulacion;

    public AlumnoEvaluacion() {
        //  this.indNotaAnulada = BigDecimal.ZERO.intValue();
        this.setMotivoAnulacion("");

    }

    public AlumnoEvaluacion(Object id) {
        this.id = TypesUtil.getLong(id);
        //    this.indNotaAnulada = BigDecimal.ZERO.intValue();
        this.setMotivoAnulacion("");
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

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public Integer getEsIngresoRegular() {
        return esIngresoRegular;
    }

    public void setEsIngresoRegular(Integer esIngresoRegular) {
        this.esIngresoRegular = esIngresoRegular;
    }

    public Usuario getUsuarioIngresoNota() {
        return usuarioIngresoNota;
    }

    public void setUsuarioIngresoNota(Usuario usuarioIngresoNota) {
        this.usuarioIngresoNota = usuarioIngresoNota;
    }

    public Date getFechaIngresoNota() {
        return fechaIngresoNota;
    }

    public void setFechaIngresoNota(Date fechaIngresoNota) {
        this.fechaIngresoNota = fechaIngresoNota;
    }

    public Long getIdReclamoNota() {
        return idReclamoNota;
    }

    public void setIdReclamoNota(Long idReclamoNota) {
        this.idReclamoNota = idReclamoNota;
    }

    public Long getIdUserAnulacion() {
        return idUserAnulacion;
    }

    public void setIdUserAnulacion(Long idUserAnulacion) {
        this.idUserAnulacion = idUserAnulacion;
    }

    public Date getFechaAnulacion() {
        return fechaAnulacion;
    }

    public void setFechaAnulacion(Date fechaAnulacion) {
        this.fechaAnulacion = fechaAnulacion;
    }

    public boolean isNotaAnulada() {
        if (this.getMotivoAnulacion() != null && !this.getMotivoAnulacion().trim().isEmpty()) {
            return true;
        }
        return false;
    }

    public String getMotivoAnulacion() {
        return motivoAnulacion;
    }

    public void setMotivoAnulacion(String motivoAnulacion) {
        this.motivoAnulacion = motivoAnulacion;
    }

    public MotivoAnulacionEnum getMotivoAnulacionEnum() {
        return MotivoAnulacionEnum.valueOf(this.getMotivoAnulacion());
    }

    public boolean isNCV() {
        if (NCV.equals(this.getNota())) {
            return true;
        }
        return false;
    }

}
