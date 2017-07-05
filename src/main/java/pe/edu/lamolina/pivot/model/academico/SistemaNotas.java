package pe.edu.lamolina.pivot.model.academico;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import pe.albatross.zelpers.miscelanea.TypesUtil;

@Entity
@Table(name = "aca_sistema_notas")
public class SistemaNotas implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "es_numerico")
    private Integer esNumerico;

    @Column(name = "valor_inicio")
    private BigDecimal valorInicio;

    @Column(name = "valor_final")
    private BigDecimal valorFinal;

    @Column(name = "minimo_aprobatorio")
    private BigDecimal minimoAprobatorio;

    @OneToMany(mappedBy = "sistemaNotas", fetch = FetchType.LAZY)
    private List<EvaluacionSeccion> evaluacionSeccion;

    @OneToMany(mappedBy = "sistemaNotas", fetch = FetchType.LAZY)
    private List<NotaLetra> notaLetra;

    @OneToMany(mappedBy = "sistemaNotas", fetch = FetchType.LAZY)
    private List<PlanCalificacion> planCalificacion;

    public SistemaNotas() {
    }

    public SistemaNotas(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getEsNumerico() {
        return esNumerico;
    }

    public void setEsNumerico(Integer esNumerico) {
        this.esNumerico = esNumerico;
    }

    public BigDecimal getValorInicio() {
        return valorInicio;
    }

    public void setValorInicio(BigDecimal valorInicio) {
        this.valorInicio = valorInicio;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public BigDecimal getMinimoAprobatorio() {
        return minimoAprobatorio;
    }

    public void setMinimoAprobatorio(BigDecimal minimoAprobatorio) {
        this.minimoAprobatorio = minimoAprobatorio;
    }

    public List<EvaluacionSeccion> getEvaluacionSeccion() {
        return evaluacionSeccion;
    }

    public void setEvaluacionSeccion(List<EvaluacionSeccion> evaluacionSeccion) {
        this.evaluacionSeccion = evaluacionSeccion;
    }

    public List<NotaLetra> getNotaLetra() {
        return notaLetra;
    }

    public void setNotaLetra(List<NotaLetra> notaLetra) {
        this.notaLetra = notaLetra;
    }

    public List<PlanCalificacion> getPlanCalificacion() {
        return planCalificacion;
    }

    public void setPlanCalificacion(List<PlanCalificacion> planCalificacion) {
        this.planCalificacion = planCalificacion;
    }

    public boolean isNumerico() {
        if (BigDecimal.ONE.intValue() == esNumerico) {
            return true;
        }
        return false;
    }

    public boolean isLetras() {
        if (BigDecimal.valueOf(0).intValue() == esNumerico) {
            return true;
        }
        return false;
    }

    public NotaLetra getNotaLetra(String letra) {
        if (this.getNotaLetra() != null && !this.getNotaLetra().isEmpty()) {
            for (NotaLetra notaLetra : this.getNotaLetra()) {
                if (notaLetra.getLetra().equals(letra)) {
                    return notaLetra;
                }
            }
        }
        return null;
    }

}
