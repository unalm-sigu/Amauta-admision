package pe.edu.lamolina.pivot.model.academico;

import java.io.Serializable;
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
import javax.validation.constraints.NotNull;
import pe.albatross.zelpers.miscelanea.TypesUtil;

@Entity
@Table(name = "aca_curso_curricula")
public class CursoCurricula implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "numero_ciclo")
    private Integer numeroCiclo;

    @NotNull
    @Column(name = "creditos")
    private Integer creditos;

    @Column(name = "creditos_requisito")
    private Integer creditosRequisito;

    @Column(name = "creditos_curricula_requisito")
    private Integer creditosCurriculaRequisito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plan_curricular")
    private PlanCurricular planCurricular;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_curso_curricula")
    private TipoCursoCurricula tipoCursoCurricula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_curso")
    private Curso curso;

    @OneToMany(mappedBy = "cursoCurricula", fetch = FetchType.LAZY)
    private List<RequisitoCursoCurricula> cursosCurricula;

    @OneToMany(mappedBy = "cursoRequisito", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RequisitoCursoCurricula> requisitosCurricula;

    public CursoCurricula() {
    }

    public CursoCurricula(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlanCurricular getPlanCurricular() {
        return planCurricular;
    }

    public void setPlanCurricular(PlanCurricular planCurricular) {
        this.planCurricular = planCurricular;
    }

    public TipoCursoCurricula getTipoCursoCurricula() {
        return tipoCursoCurricula;
    }

    public void setTipoCursoCurricula(TipoCursoCurricula tipoCursoCurricula) {
        this.tipoCursoCurricula = tipoCursoCurricula;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Integer getNumeroCiclo() {
        return numeroCiclo;
    }

    public void setNumeroCiclo(Integer numeroCiclo) {
        this.numeroCiclo = numeroCiclo;
    }

    public Integer getCreditos() {
        return creditos;
    }

    public void setCreditos(Integer creditos) {
        this.creditos = creditos;
    }

    public Integer getCreditosRequisito() {
        return creditosRequisito;
    }

    public void setCreditosRequisito(Integer creditosRequisito) {
        this.creditosRequisito = creditosRequisito;
    }

    public Integer getCreditosCurriculaRequisito() {
        return creditosCurriculaRequisito;
    }

    public void setCreditosCurriculaRequisito(Integer creditosCurriculaRequisito) {
        this.creditosCurriculaRequisito = creditosCurriculaRequisito;
    }

    public List<RequisitoCursoCurricula> getCursosCurricula() {
        return cursosCurricula;
    }

    public void setCursosCurricula(List<RequisitoCursoCurricula> cursosCurricula) {
        this.cursosCurricula = cursosCurricula;
    }

    public List<RequisitoCursoCurricula> getRequisitosCurricula() {
        return requisitosCurricula;
    }

    public void setRequisitosCurricula(List<RequisitoCursoCurricula> requisitosCurricula) {
        this.requisitosCurricula = requisitosCurricula;
    }

}
