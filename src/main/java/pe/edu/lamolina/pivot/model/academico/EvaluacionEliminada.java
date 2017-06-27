/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import pe.edu.lamolina.pivot.model.seguridad.Usuario;

@Entity
@Table(name = "aca_evaluacion_eliminada")
public class EvaluacionEliminada implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "peso")
    private BigDecimal peso;

    @Column(name = "esta_desagregado")
    private Integer estaDesagregado;

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

    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaRegistro;

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
    @JoinColumn(name = "id_seccion_responsable")
    private Seccion seccionResponsable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evaluacion_elim_superior")
    private EvaluacionEliminada evaluacionEliminadaSuperior;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evaluador")
    private Docente docenteEvaluador;

    @Column(name = "ind_porcentaje_variable")
    @NotNull
    private Integer indPorcentajeVariable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_registro")
    private Usuario usuarioRegistro;

    @OneToMany(mappedBy = "evaluacionEliminada", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AlumnoEvaluacionElim> alumnoEvaluacionElims;

    public void create(Evaluacion evaluacion) {
        this.setDocenteEvaluador(evaluacion.getDocenteEvaluador());
        this.setEstaDesagregado(evaluacion.getEstaDesagregado());
        //  this.setEvaluacionEliminadaSuperior(evaluacion.getEvaluacionSuperior());
        this.setEvaluacionSeccion(evaluacion.getEvaluacionSeccion());
        this.setEvaluados(evaluacion.getEvaluados());
        this.setExtemporaneos(evaluacion.getExtemporaneos());
        this.setFechaIngresoNota(evaluacion.getFechaIngresoNota());
        this.setFechaProgramada(evaluacion.getFechaProgramada());
        this.setFechaRealizada(evaluacion.getFechaRealizada());
        this.setIndPorcentajeVariable(evaluacion.getIndPorcentajeVariable());
        this.setNumero(evaluacion.getNumero());
        this.setPeso(evaluacion.getPeso());
        this.setSeccionResponsable(evaluacion.getSeccionResponsable());
        this.setTipoEvaluacion(evaluacion.getTipoEvaluacion());
        this.setTipoSeccion(evaluacion.getTipoSeccion());

        //   this.setEvaluacionEliminadaSuperior(  );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTipoSeccion() {
        return tipoSeccion;
    }

    public void setTipoSeccion(String tipoSeccion) {
        this.tipoSeccion = tipoSeccion;
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

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
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

    public Seccion getSeccionResponsable() {
        return seccionResponsable;
    }

    public void setSeccionResponsable(Seccion seccionResponsable) {
        this.seccionResponsable = seccionResponsable;
    }

    public EvaluacionEliminada getEvaluacionEliminadaSuperior() {
        return evaluacionEliminadaSuperior;
    }

    public void setEvaluacionEliminadaSuperior(EvaluacionEliminada evaluacionEliminadaSuperior) {
        this.evaluacionEliminadaSuperior = evaluacionEliminadaSuperior;
    }

    public Docente getDocenteEvaluador() {
        return docenteEvaluador;
    }

    public void setDocenteEvaluador(Docente docenteEvaluador) {
        this.docenteEvaluador = docenteEvaluador;
    }

    public Integer getIndPorcentajeVariable() {
        return indPorcentajeVariable;
    }

    public void setIndPorcentajeVariable(Integer indPorcentajeVariable) {
        this.indPorcentajeVariable = indPorcentajeVariable;
    }

    public Usuario getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(Usuario usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    public List<AlumnoEvaluacionElim> getAlumnoEvaluacionElims() {
        return alumnoEvaluacionElims;
    }

    public void setAlumnoEvaluacionElims(List<AlumnoEvaluacionElim> alumnoEvaluacionElims) {
        this.alumnoEvaluacionElims = alumnoEvaluacionElims;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

}
