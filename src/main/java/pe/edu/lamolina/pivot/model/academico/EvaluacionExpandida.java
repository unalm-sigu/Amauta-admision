package pe.edu.lamolina.pivot.model.academico;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.validation.constraints.NotNull;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoSeccionEvalEnum;

@Entity
@Table(name = "aca_evaluacion_expandida")
public class EvaluacionExpandida implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "peso")
    private BigDecimal peso;

    @Column(name = "numero")
    private Integer numero;

    @Column(name = "nivel")
    private Integer nivel;

    @Column(name = "esta_desagregado")
    private Integer estaDesagregado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_desagregar")
    private Usuario usuarioDesagregar;

    @Column(name = "fecha_desagregar")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaDesagregar;

    @Column(name = "tipo_seccion")
    private String tipoSeccion;

//    @Column(name = "fecha_programada")
//    @Temporal(javax.persistence.TemporalType.DATE)
//    private Date fechaProgramada;
//
//    @Column(name = "fecha_realizada")
//    @Temporal(javax.persistence.TemporalType.DATE)
//    private Date fechaRealizada;
//
//    @Column(name = "fecha_ingreso_nota")
//    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
//    private Date fechaIngresoNota;

//    @Column(name = "evaluados")
//    private Integer evaluados;
//
//    @Column(name = "extemporaneos")
//    private Integer extemporaneos;

    @Column(name = " ind_notas_ingresadas")
    private Integer indNotasIngresadas;

    @NotNull
    @Column(name = "ind_porcentaje_variable")
    private Integer indPorcentajeVariable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evaluacion_seccion")
    private EvaluacionSeccion evaluacionSeccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_evaluacion")
    private TipoEvaluacion tipoEvaluacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evaluacion_superior")
    private EvaluacionExpandida evaluacionSuperior;

    @Column(name = "nota_minima_anulable")
    @NotNull
    private Integer notaMinimaAnulable;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "id_seccion_responsable")
//    private Seccion seccionResponsable;
    @OneToMany(mappedBy = "evaluacion", fetch = FetchType.LAZY)
    private List<AlumnoEvaluacion> alumnoEvaluacion;

    @OneToMany(mappedBy = "evaluacionSuperior", fetch = FetchType.LAZY)
    private List<EvaluacionExpandida> evaluacionesExpandidas;

    @OneToMany(mappedBy = "evaluacionExpandida", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Evaluacion> evaluaciones;

    @OneToMany(mappedBy = "evaluacion", fetch = FetchType.LAZY)
    private List<ReclamoNota> reclamoNota;

    @Column(name = "estado")
    private String estado;

    public EvaluacionExpandida() {
        this.estado = EstadoEnum.ACT.name();
        this.indNotasIngresadas = BigDecimal.ZERO.intValue();
    }

    public EvaluacionExpandida(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EvaluacionSeccion getEvaluacionSeccion() {
        return evaluacionSeccion;
    }

    public void setEvaluacionSeccion(EvaluacionSeccion evaluacionSeccion) {
        this.evaluacionSeccion = evaluacionSeccion;
    }

    public TipoEvaluacion getTipoEvaluacion() {
        return tipoEvaluacion;
    }

    public void setTipoEvaluacion(TipoEvaluacion tipoEvaluacion) {
        this.tipoEvaluacion = tipoEvaluacion;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public Integer getEstaDesagregado() {
        return estaDesagregado;
    }

    public void setEstaDesagregado(Integer estaDesagregado) {
        this.estaDesagregado = estaDesagregado;
    }

    public EvaluacionExpandida getEvaluacionSuperior() {
        return evaluacionSuperior;
    }

    public void setEvaluacionSuperior(EvaluacionExpandida evaluacionSuperior) {
        this.evaluacionSuperior = evaluacionSuperior;
    }

    public Usuario getUsuarioDesagregar() {
        return usuarioDesagregar;
    }

    public void setUsuarioDesagregar(Usuario usuarioDesagregar) {
        this.usuarioDesagregar = usuarioDesagregar;
    }

    public Date getFechaDesagregar() {
        return fechaDesagregar;
    }

    public void setFechaDesagregar(Date fechaDesagregar) {
        this.fechaDesagregar = fechaDesagregar;
    }

    public String getTipoSeccion() {
        return tipoSeccion;
    }

    public void setTipoSeccion(String tipoSeccion) {
        this.tipoSeccion = tipoSeccion;
    }

    public void setTipoSeccionEvalEnum(TipoSeccionEvalEnum tipoSeccionEvalEnum) {
        this.tipoSeccion = tipoSeccionEvalEnum.name();
    }

//    public Seccion getSeccionResponsable() {
//        return seccionResponsable;
//    }
//
//    public void setSeccionResponsable(Seccion seccionResponsable) {
//        this.seccionResponsable = seccionResponsable;
//    }
//    public Date getFechaProgramada() {
//        return fechaProgramada;
//    }
//
//    public void setFechaProgramada(Date fechaProgramada) {
//        this.fechaProgramada = fechaProgramada;
//    }
//
//    public Date getFechaRealizada() {
//        return fechaRealizada;
//    }
//
//    public void setFechaRealizada(Date fechaRealizada) {
//        this.fechaRealizada = fechaRealizada;
//    }
//
//    public Date getFechaIngresoNota() {
//        return fechaIngresoNota;
//    }
//
//    public void setFechaIngresoNota(Date fechaIngresoNota) {
//        this.fechaIngresoNota = fechaIngresoNota;
//    }

//    public Integer getEvaluados() {
//        return evaluados;
//    }
//
//    public void setEvaluados(Integer evaluados) {
//        this.evaluados = evaluados;
//    }
//
//    public Integer getExtemporaneos() {
//        return extemporaneos;
//    }
//
//    public void setExtemporaneos(Integer extemporaneos) {
//        this.extemporaneos = extemporaneos;
//    }

    public List<AlumnoEvaluacion> getAlumnoEvaluacion() {
        return alumnoEvaluacion;
    }

    public void setAlumnoEvaluacion(List<AlumnoEvaluacion> alumnoEvaluacion) {
        this.alumnoEvaluacion = alumnoEvaluacion;
    }

    public List<EvaluacionExpandida> getEvaluacionesExpandidas() {
        return evaluacionesExpandidas;
    }

    public void setEvaluacionesExpandidas(List<EvaluacionExpandida> evaluacionesExpandidas) {
        this.evaluacionesExpandidas = evaluacionesExpandidas;
    }

    public List<ReclamoNota> getReclamoNota() {
        return reclamoNota;
    }

    public void setReclamoNota(List<ReclamoNota> reclamoNota) {
        this.reclamoNota = reclamoNota;
    }

    public TipoSeccionEvalEnum getTipoSeccionEvalEnum() {
        return TipoSeccionEvalEnum.valueOf(tipoSeccion);
    }

    public void setTipoSeccionEnum(TipoSeccionEvalEnum tipoSeccionEvalEnum) {
        this.setTipoSeccion(tipoSeccionEvalEnum.name());
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public List<Evaluacion> getEvaluaciones() {
        return evaluaciones;
    }

    public void setEvaluaciones(List<Evaluacion> evaluaciones) {
        this.evaluaciones = evaluaciones;
    }

    public Integer getIndNotasIngresadas() {
        return indNotasIngresadas;
    }

    public void setIndNotasIngresadas(Integer indNotasIngresadas) {
        this.indNotasIngresadas = indNotasIngresadas;
    }

    public Integer getNotaMinimaAnulable() {
        return notaMinimaAnulable;
    }

    public void setNotaMinimaAnulable(Integer notaMinimaAnulable) {
        this.notaMinimaAnulable = notaMinimaAnulable;
    }

    public void create(EvaluacionSeccion evalSeccion, EvaluacionPlan evaluacionPlan, Integer numero) {
        this.setAlumnoEvaluacion(null);
        this.setEvaluacionSeccion(evalSeccion);
        this.setTipoEvaluacion(evaluacionPlan.getTipoEvaluacion());
        //     this.setTipoSeccion(evaluacionPlan.getTipoSeccion());
        this.setEstaDesagregado(BigDecimal.ZERO.intValue());
        this.setEvaluacionSuperior(null);
        this.setEvaluacionesExpandidas(null);
        //this.setEvaluados(BigDecimal.ZERO.intValue());
        this.setPeso(evaluacionPlan.getPesoEvaluacion());
        this.setNumero(numero);
        this.setIndPorcentajeVariable(evaluacionPlan.getIndPorcentajeVariable());
        this.setNotaMinimaAnulable(BigDecimal.ZERO.intValue());
    }

    public boolean isDesagregado() {
        if (BigDecimal.ONE.intValue() == this.getEstaDesagregado()) {
            return true;
        }
        return false;
    }

    public boolean isNotasIngresadas() {
        if (this.getIndNotasIngresadas() == null) {
            return false;
        }
        if (BigDecimal.ONE.intValue() == this.getIndNotasIngresadas()) {
            return true;
        }
        return false;
    }

    public Integer getIndPorcentajeVariable() {
        return indPorcentajeVariable;
    }

    public void setIndPorcentajeVariable(Integer indPorcentajeVariable) {
        this.indPorcentajeVariable = indPorcentajeVariable;
    }

    public boolean isPorcentajeVariable() {
        if (BigDecimal.ONE.intValue() == this.getIndPorcentajeVariable().intValue()) {
            return true;
        }
        return false;
    }

    public boolean isEstadoAnulado() {
        if (EstadoEnum.ANU.name().equals(this.getEstado())) {
            return true;
        }
        return false;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setEstadoEnum(EstadoEnum estadoEnum) {
        this.estado = estadoEnum.name();
    }

    public EstadoEnum getEstadoEnum() {
        return EstadoEnum.valueOf(this.getEstado());
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

}
