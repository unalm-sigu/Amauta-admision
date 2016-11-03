package pe.edu.lamolina.pivot.model.academico;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.zelper.enums.EstadoPlanCalificaEnum;

@Entity
@Table(name = "aca_plan_calificacion")
public class PlanCalificacion implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fecha_aprobacion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaAprobacion;

    @Column(name = "numero")
    private Long numero;

    @Column(name = "nota_base")
    private Integer notaBase;

    @Column(name = "id_user_registro")
    private Long idUserRegistro;

    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_departamento_academico")
    private DepartamentoAcademico departamentoAcademico;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_sistema_notas")
    private SistemaNotas sistemaNotas;

    @OneToMany(mappedBy = "sistemaEvaluacion", fetch = FetchType.LAZY)
    private List<Curso> curso;

    @OneToMany(mappedBy = "planCalificacion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<EvaluacionPlan> evaluacionPlan;

    @OneToMany(mappedBy = "planCalificacion", fetch = FetchType.LAZY)
    private List<EvaluacionSeccion> evaluacionSeccion;

    @OneToMany(mappedBy = "sistemaAcademico", fetch = FetchType.LAZY)
    private List<LoggerPlanCalificacion> loggerPlanCalificacion;

    public PlanCalificacion() {
    }

    public PlanCalificacion(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DepartamentoAcademico getDepartamentoAcademico() {
        return departamentoAcademico;
    }

    public void setDepartamentoAcademico(DepartamentoAcademico departamentoAcademico) {
        this.departamentoAcademico = departamentoAcademico;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public EstadoPlanCalificaEnum getEstadoEnum() {
        if (estado.isEmpty()) {
            return null;
        }
        return EstadoPlanCalificaEnum.valueOf(estado);
    }

    public void setEstadoEnum(EstadoPlanCalificaEnum estadoPlanCalificaEnum) {
        this.estado = estadoPlanCalificaEnum.name();
    }

    public Date getFechaAprobacion() {
        return fechaAprobacion;
    }

    public void setFechaAprobacion(Date fechaAprobacion) {
        this.fechaAprobacion = fechaAprobacion;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public SistemaNotas getSistemaNotas() {
        return sistemaNotas;
    }

    public void setSistemaNotas(SistemaNotas sistemaNotas) {
        this.sistemaNotas = sistemaNotas;
    }

    public Integer getNotaBase() {
        return notaBase;
    }

    public void setNotaBase(Integer notaBase) {
        this.notaBase = notaBase;
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

    public List<Curso> getCurso() {
        return curso;
    }

    public void setCurso(List<Curso> curso) {
        this.curso = curso;
    }

    public List<EvaluacionPlan> getEvaluacionPlan() {
        return evaluacionPlan;
    }

    public void setEvaluacionPlan(List<EvaluacionPlan> evaluacionPlan) {
        this.evaluacionPlan = evaluacionPlan;
    }

    public List<EvaluacionSeccion> getEvaluacionSeccion() {
        return evaluacionSeccion;
    }

    public void setEvaluacionSeccion(List<EvaluacionSeccion> evaluacionSeccion) {
        this.evaluacionSeccion = evaluacionSeccion;
    }

    public List<LoggerPlanCalificacion> getLoggerPlanCalificacion() {
        return loggerPlanCalificacion;
    }

    public void setLoggerPlanCalificacion(List<LoggerPlanCalificacion> loggerPlanCalificacion) {
        this.loggerPlanCalificacion = loggerPlanCalificacion;
    }

    @Override
    public String toString() {
        return "PlanCalificacion{" + "id=" + id + ", estado=" + estado + ", fechaAprobacion=" + fechaAprobacion + ", numero=" + numero + ", notaBase=" + notaBase + ", idUserRegistro=" + idUserRegistro + ", fechaRegistro=" + fechaRegistro + ", departamentoAcademico=" + departamentoAcademico + ", sistemaNotas=" + sistemaNotas + '}';
    }

}
