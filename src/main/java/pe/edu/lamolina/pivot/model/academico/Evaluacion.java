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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.enums.TipoSeccionEvalEnum;

@Entity
@Table(name = "aca_evaluacion")
public class Evaluacion implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "peso")
    private BigDecimal peso;

    @Column(name = "esta_desagregado")
    private Integer estaDesagregado;

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

    @Column(name = "fecha_ingreso_nota")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaIngresoNota;

    @Column(name = "evaluados")
    private Integer evaluados;

    @Column(name = "extemporaneos")
    private Integer extemporaneos;

    @Column(name = "numero")
    private Integer numero;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evaluacion_expandida")
    private EvaluacionExpandida evaluacionExpandida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_desagregar")
    private Usuario usuarioDesagregar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evaluador")
    private Docente docenteEvaluador;

    @OneToMany(mappedBy = "evaluacion", fetch = FetchType.LAZY)
    private List<AlumnoEvaluacion> alumnoEvaluacion;

    @OneToMany(mappedBy = "evaluacionSuperior", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Evaluacion> evaluaciones;

    @OneToMany(mappedBy = "evaluacion", fetch = FetchType.LAZY)
    private List<ReclamoNota> reclamoNota;

    @Transient
    private List<DocenteSeccion> docentesSeccion;

    @Transient
    private boolean notasIngresadas;

    @Column(name = "ind_porcentaje_variable")
    @NotNull
    private Integer indPorcentajeVariable;

    @Transient
    private String nombreCorto;

    @Transient
    private String nombreLargo;

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

    public Evaluacion getEvaluacionSuperior() {
        return evaluacionSuperior;
    }

    public void setEvaluacionSuperior(Evaluacion evaluacionSuperior) {
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

    public List<Evaluacion> getEvaluaciones() {
        return evaluaciones;
    }

    public void setEvaluaciones(List<Evaluacion> evaluaciones) {
        this.evaluaciones = evaluaciones;
    }

    public List<ReclamoNota> getReclamoNota() {
        return reclamoNota;
    }

    public void setReclamoNota(List<ReclamoNota> reclamoNota) {
        this.reclamoNota = reclamoNota;
    }

    public TipoSeccionEvalEnum getTipoSeccionEnum() {
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

    public boolean isDesagregado() {
        if (BigDecimal.ONE.intValue() == this.getEstaDesagregado().intValue()) {
            return true;
        }
        return false;
    }

    public EvaluacionExpandida getEvaluacionExpandida() {
        return evaluacionExpandida;
    }

    public void setEvaluacionExpandida(EvaluacionExpandida evaluacionExpandida) {
        this.evaluacionExpandida = evaluacionExpandida;
    }

    public List<DocenteSeccion> getDocentesSeccion() {
        return docentesSeccion;
    }

    public void setDocentesSeccion(List<DocenteSeccion> docentesSeccion) {
        this.docentesSeccion = docentesSeccion;
    }

    public Docente getDocenteEvaluador() {
        return docenteEvaluador;
    }

    public void setDocenteEvaluador(Docente docenteEvaluador) {
        this.docenteEvaluador = docenteEvaluador;
    }

    public boolean isNotasIngresadas() {
        return notasIngresadas;
    }

    public void setNotasIngresadas(boolean notasIngresadas) {
        this.notasIngresadas = notasIngresadas;
    }

    /*
    public void create(EvaluacionSeccion evalSeccion, EvaluacionPlan evaluacionPlan) {
        this.setAlumnoEvaluacion(null);
        this.setEvaluacionSeccion(evalSeccion);
        this.setTipoEvaluacion(evaluacionPlan.getTipoEvaluacion());
        this.setTipoSeccion(evaluacionPlan.getTipoSeccion());
        this.setEstaDesagregado(BigDecimal.ZERO.intValue());
        this.setEvaluacionSuperior(null);
        this.setEvaluaciones(null);
        this.setEvaluados(BigDecimal.ZERO.intValue());
        this.setPeso(evaluacionPlan.getPesoEvaluacion());
    }
     */
    public void create(EvaluacionSeccion evalSeccion, Seccion seccion, EvaluacionExpandida evaluacionExpandida) {
        this.setAlumnoEvaluacion(null);
        this.setEvaluacionSeccion(evalSeccion);
        this.setTipoEvaluacion(evaluacionExpandida.getTipoEvaluacion());
        this.setTipoSeccion(evaluacionExpandida.getTipoSeccion());
        this.setEvaluacionSuperior(null);
        this.setEvaluaciones(null);
        this.setEvaluados(BigDecimal.ZERO.intValue());
        this.setPeso(evaluacionExpandida.getPeso());
        this.setSeccionResponsable(seccion);
        this.setNumero(evaluacionExpandida.getNumero());

        this.setEstaDesagregado(evaluacionExpandida.getEstaDesagregado());
        this.setFechaDesagregar(evaluacionExpandida.getFechaDesagregar());
        this.setUsuarioDesagregar(evaluacionExpandida.getUsuarioDesagregar());
        this.setEvaluacionExpandida(evaluacionExpandida);
        this.setIndPorcentajeVariable(evaluacionExpandida.getIndPorcentajeVariable());
    }

    public Integer getIndPorcentajeVariable() {
        return indPorcentajeVariable;
    }

    public void setIndPorcentajeVariable(Integer indPorcentajeVariable) {
        this.indPorcentajeVariable = indPorcentajeVariable;
    }

    public boolean getEsHijo() {
        if (ObjectUtil.getParentTree(this, "evaluacionSuperior.id") != null) {
            return true;
        }
        return false;
    }

    public String getNombreCorto() {
        return nombreCorto;
    }

    public void setNombreCorto(String nombreCorto) {
        this.nombreCorto = nombreCorto;
    }

    public String getNombreLargo() {
        return nombreLargo;
    }

    public void setNombreLargo(String nombreLargo) {
        this.nombreLargo = nombreLargo;
    }

    public String getCodigoNumeroGen() {
        StringBuilder nombre = new StringBuilder();
        if (ObjectUtil.getParentTree(this, "evaluacionSuperior.id") != null
                && ObjectUtil.getParentTree(this, "evaluacionSuperior.tipoEvaluacion.id") != null) {

            Evaluacion evaluacionPadre = this.getEvaluacionSuperior();
            nombre.append("(");
            nombre.append(evaluacionPadre.getTipoEvaluacion().getCodigo());
            nombre.append(evaluacionPadre.getNumero());
            nombre.append(")");
        }
        nombre.append(this.getTipoEvaluacion().getCodigo());
        nombre.append(this.getNumero());
        nombre.append(" - ");
        nombre.append(this.getTipoEvaluacion().getNombre());
        nombre.append(" ");
        nombre.append(this.getNumero());
        return nombre.toString();
    }

}
