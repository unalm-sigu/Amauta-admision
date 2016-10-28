package pe.edu.lamolina.pivot.model.academico;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
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

@Entity
@Table(name = "aca_evaluacion")
public class Evaluacion implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "peso")
    private String peso;

    @Column(name = "esta_desagregado")
    private Integer estaDesagregado;

    @Column(name = "id_user_desagregar")
    private Long idUserDesagregar;

    @Column(name = "fecha_desagregar")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaDesagregar;

    @Column(name = "tipo_seccion")
    private String tipoSeccion;

    @Column(name = "fecha_programada")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaProgramada;

    @Column(name = "fecha_realizada")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaRealizada;

    @Column(name = "id_evaluador")
    private Long idEvaluador;

    @Column(name = "fecha_ingreso_nota")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaIngresoNota;

    @Column(name = "evaluados")
    private Integer evaluados;

    @Column(name = "extemporaneos")
    private Integer extemporaneos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evaluacion_seccion")
    private EvaluacionSeccion evaluacionSeccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_evaluacion")
    private TipoEvaluacion tipoEvaluacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evaluacion_superior")
    private Evaluacion evaluacionSuperior;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_seccion_responsable")
    private Seccion seccionResponsable;

    @OneToMany(mappedBy = "evaluacion", fetch = FetchType.LAZY)
    private List<AlumnoEvaluacion> alumnoEvaluacion;

    @OneToMany(mappedBy = "evaluacionSuperior", fetch = FetchType.LAZY)
    private List<Evaluacion> evaluacion;

    @OneToMany(mappedBy = "evaluacion", fetch = FetchType.LAZY)
    private List<ReclamoNota> reclamoNota;

    public Evaluacion() {
    }

    public Evaluacion(Object id) {
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

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public Integer getEstaDesagregado() {
        return estaDesagregado;
    }

    public void setEstaDesagregado(Integer estaDesagregado) {
        this.estaDesagregado = estaDesagregado;
    }

    public Evaluacion getEvaluacionSuperior() {
        return evaluacionSuperior;
    }

    public void setEvaluacionSuperior(Evaluacion evaluacionSuperior) {
        this.evaluacionSuperior = evaluacionSuperior;
    }

    public Long getIdUserDesagregar() {
        return idUserDesagregar;
    }

    public void setIdUserDesagregar(Long idUserDesagregar) {
        this.idUserDesagregar = idUserDesagregar;
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

    public Seccion getSeccionResponsable() {
        return seccionResponsable;
    }

    public void setSeccionResponsable(Seccion seccionResponsable) {
        this.seccionResponsable = seccionResponsable;
    }

    public Date getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(Date fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public Date getFechaRealizada() {
        return fechaRealizada;
    }

    public void setFechaRealizada(Date fechaRealizada) {
        this.fechaRealizada = fechaRealizada;
    }

    public Long getIdEvaluador() {
        return idEvaluador;
    }

    public void setIdEvaluador(Long idEvaluador) {
        this.idEvaluador = idEvaluador;
    }

    public Date getFechaIngresoNota() {
        return fechaIngresoNota;
    }

    public void setFechaIngresoNota(Date fechaIngresoNota) {
        this.fechaIngresoNota = fechaIngresoNota;
    }

    public Integer getEvaluados() {
        return evaluados;
    }

    public void setEvaluados(Integer evaluados) {
        this.evaluados = evaluados;
    }

    public Integer getExtemporaneos() {
        return extemporaneos;
    }

    public void setExtemporaneos(Integer extemporaneos) {
        this.extemporaneos = extemporaneos;
    }

    public List<AlumnoEvaluacion> getAlumnoEvaluacion() {
        return alumnoEvaluacion;
    }

    public void setAlumnoEvaluacion(List<AlumnoEvaluacion> alumnoEvaluacion) {
        this.alumnoEvaluacion = alumnoEvaluacion;
    }

    public List<Evaluacion> getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(List<Evaluacion> evaluacion) {
        this.evaluacion = evaluacion;
    }

    public List<ReclamoNota> getReclamoNota() {
        return reclamoNota;
    }

    public void setReclamoNota(List<ReclamoNota> reclamoNota) {
        this.reclamoNota = reclamoNota;
    }

}

