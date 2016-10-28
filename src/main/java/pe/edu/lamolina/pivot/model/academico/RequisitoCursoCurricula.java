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
@Table(name = "aca_requisito_curso_curricula")
public class RequisitoCursoCurricula implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_curso_curricula")
    private CursoCurricula cursoCurricula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_curso_requisito")
    private CursoCurricula cursoRequisito;

    public RequisitoCursoCurricula() {
    }

    public RequisitoCursoCurricula(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CursoCurricula getCursoCurricula() {
        return cursoCurricula;
    }

    public void setCursoCurricula(CursoCurricula cursoCurricula) {
        this.cursoCurricula = cursoCurricula;
    }

    public CursoCurricula getCursoRequisito() {
        return cursoRequisito;
    }

    public void setCursoRequisito(CursoCurricula cursoRequisito) {
        this.cursoRequisito = cursoRequisito;
    }

}

