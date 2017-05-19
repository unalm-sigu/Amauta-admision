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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

@Entity
@Table(name = "aca_plan_calificacion_curso")
@NamedQueries({
    @NamedQuery(name = "PlanCalificacionCurso.findAll", query = "SELECT p FROM PlanCalificacionCurso p")})
public class PlanCalificacionCurso implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "fecha_actualizacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaActualizacion;

    @NotNull
    @Column(name = "estado")
    private String estado;

    @JoinColumn(name = "id_plan_calificacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private PlanCalificacion planCalificacion;

    @JoinColumn(name = "id_curso", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Curso curso;

    public PlanCalificacionCurso() {
    }

    public PlanCalificacionCurso(Long id) {
        this.id = id;
    }

    public PlanCalificacionCurso(Long id, Date fechaCreacion, String estado) {
        this.id = id;
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public EstadoEnum getEstadoEnum() {
        return EstadoEnum.valueOf(this.getEstado());
    }

    public void setEstadoEnum(EstadoEnum estadoEnum) {
        this.estado = estadoEnum.name();
    }

    public PlanCalificacion getPlanCalificacion() {
        return planCalificacion;
    }

    public void setPlanCalificacion(PlanCalificacion planCalificacion) {
        this.planCalificacion = planCalificacion;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
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
        if (!(object instanceof PlanCalificacionCurso)) {
            return false;
        }
        PlanCalificacionCurso other = (PlanCalificacionCurso) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pe.edu.lamolina.pivot.model.academico.PlanCalificacionCurso[ id=" + id + " ]";
    }

}
