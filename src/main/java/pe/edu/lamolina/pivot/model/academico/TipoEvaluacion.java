package pe.edu.lamolina.pivot.model.academico;

import java.io.Serializable;
import java.math.BigDecimal;
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
import pe.albatross.zelpers.miscelanea.TypesUtil;

@Entity
@Table(name = "aca_tipo_evaluacion")
public class TipoEvaluacion implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "numero_subevaluacion")
    private Integer numeroSubevaluacion;

    @Column(name = "es_divisible")
    private Integer esDivisible;

    @Column(name = "cantidad_maxima")
    private Integer cantidadMaxima;

    @Column(name = "ind_nota_minima_anulable")
    private Integer indNotaMinimaAnulable;

    @Column(name = "orden")
    private Integer orden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evaluacion_superior")
    private TipoEvaluacion evaluacionSuperior;

    @OneToMany(mappedBy = "tipoEvaluacion", fetch = FetchType.LAZY)
    private List<Evaluacion> evaluacion;

    @OneToMany(mappedBy = "tipoEvaluacion", fetch = FetchType.LAZY)
    private List<EvaluacionPlan> evaluacionPlan;

    @OneToMany(mappedBy = "evaluacionSuperior", fetch = FetchType.LAZY)
    private List<TipoEvaluacion> tipoEvaluacion;

    public TipoEvaluacion() {
    }

    public TipoEvaluacion(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoEvaluacion getEvaluacionSuperior() {
        return evaluacionSuperior;
    }

    public void setEvaluacionSuperior(TipoEvaluacion evaluacionSuperior) {
        this.evaluacionSuperior = evaluacionSuperior;
    }

    public Integer getNumeroSubevaluacion() {
        return numeroSubevaluacion;
    }

    public void setNumeroSubevaluacion(Integer numeroSubevaluacion) {
        this.numeroSubevaluacion = numeroSubevaluacion;
    }

    public Integer getEsDivisible() {
        return esDivisible;
    }

    public void setEsDivisible(Integer esDivisible) {
        this.esDivisible = esDivisible;
    }

    public List<Evaluacion> getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(List<Evaluacion> evaluacion) {
        this.evaluacion = evaluacion;
    }

    public List<EvaluacionPlan> getEvaluacionPlan() {
        return evaluacionPlan;
    }

    public void setEvaluacionPlan(List<EvaluacionPlan> evaluacionPlan) {
        this.evaluacionPlan = evaluacionPlan;
    }

    public List<TipoEvaluacion> getTipoEvaluacion() {
        return tipoEvaluacion;
    }

    public void setTipoEvaluacion(List<TipoEvaluacion> tipoEvaluacion) {
        this.tipoEvaluacion = tipoEvaluacion;
    }

    public Integer getCantidadMaxima() {
        return cantidadMaxima;
    }

    public void setCantidadMaxima(Integer cantidadMaxima) {
        this.cantidadMaxima = cantidadMaxima;
    }

    public Integer getIndNotaMinimaAnulable() {
        return indNotaMinimaAnulable;
    }

    public void setIndNotaMinimaAnulable(Integer indNotaMinimaAnulable) {
        this.indNotaMinimaAnulable = indNotaMinimaAnulable;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public boolean isNotaMinimaAnulable() {
        if (BigDecimal.ONE.intValue() == this.indNotaMinimaAnulable) {
            return true;
        }
        return false;
    }

}
