package pe.edu.lamolina.pivot.model.academico;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.zelper.enums.TipoSeccionEvalEnum;

@Entity
@Table(name = "aca_evaluacion_plan")
public class EvaluacionPlan implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "cantidad_evaluaciones")
    private Integer cantidadEvaluaciones;

    @Column(name = "evaluaciones_obligatorias")
    private Integer evaluacionesObligatorias;

    @Column(name = "peso_evaluacion")
    private Integer pesoEvaluacion;

    @Column(name = "peso_total")
    private Integer pesoTotal;

    @Column(name = "tipo_seccion")
    private String tipoSeccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plan_calificacion")
    private PlanCalificacion planCalificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_evaluacion")
    private TipoEvaluacion tipoEvaluacion;

    @Column(name = "nota_minima_anulable")
    private Integer notaMinimaAnulable;

    public EvaluacionPlan() {
    }

    public EvaluacionPlan(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlanCalificacion getPlanCalificacion() {
        return planCalificacion;
    }

    public void setPlanCalificacion(PlanCalificacion planCalificacion) {
        this.planCalificacion = planCalificacion;
    }

    public TipoEvaluacion getTipoEvaluacion() {
        return tipoEvaluacion;
    }

    public void setTipoEvaluacion(TipoEvaluacion tipoEvaluacion) {
        this.tipoEvaluacion = tipoEvaluacion;
    }

    public Integer getCantidadEvaluaciones() {
        return cantidadEvaluaciones;
    }

    public void setCantidadEvaluaciones(Integer cantidadEvaluaciones) {
        this.cantidadEvaluaciones = cantidadEvaluaciones;
    }

    public Integer getEvaluacionesObligatorias() {
        return evaluacionesObligatorias;
    }

    public void setEvaluacionesObligatorias(Integer evaluacionesObligatorias) {
        this.evaluacionesObligatorias = evaluacionesObligatorias;
    }

    public Integer getPesoEvaluacion() {
        return pesoEvaluacion;
    }

    public void setPesoEvaluacion(Integer pesoEvaluacion) {
        this.pesoEvaluacion = pesoEvaluacion;
    }

    public Integer getPesoTotal() {
        return pesoTotal;
    }

    public void setPesoTotal(Integer pesoTotal) {
        this.pesoTotal = pesoTotal;
    }

    public String getTipoSeccion() {
        return tipoSeccion;
    }

    public void setTipoSeccion(String tipoSeccion) {
        this.tipoSeccion = tipoSeccion;
    }

    public TipoSeccionEvalEnum getTipoSeccionEnum() {
        return TipoSeccionEvalEnum.valueOf(tipoSeccion);
    }

    public Integer getNotaMinimaAnulable() {
        return notaMinimaAnulable;
    }

    public void setNotaMinimaAnulable(Integer notaMinimaAnulable) {
        this.notaMinimaAnulable = notaMinimaAnulable;
    }

}
