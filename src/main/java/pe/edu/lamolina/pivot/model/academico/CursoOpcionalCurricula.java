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
import pe.albatross.zelpers.miscelanea.TypesUtil;

@Entity
@Table(name = "aca_curso_opcional_curricula")
public class CursoOpcionalCurricula implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

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

    public CursoOpcionalCurricula() {
    }

    public CursoOpcionalCurricula(Object id) {
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

}

