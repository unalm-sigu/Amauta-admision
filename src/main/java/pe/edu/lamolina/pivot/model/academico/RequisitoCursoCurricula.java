package pe.edu.lamolina.pivot.model.academico;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;

@Entity
@Table(name = "aca_requisito_curso_curricula")
public class RequisitoCursoCurricula implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "simultaneo")
    private Integer simultaneo;

    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_curso_curricula")
    private CursoCurricula cursoCurricula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_curso_requisito")
    private CursoCurricula cursoRequisito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_registro")
    private Usuario userRegistro;

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

    public Integer getSimultaneo() {
        return simultaneo;
    }

    public void setSimultaneo(Integer simultaneo) {
        this.simultaneo = simultaneo;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Usuario getUserRegistro() {
        return userRegistro;
    }

    public void setUserRegistro(Usuario userRegistro) {
        this.userRegistro = userRegistro;
    }

}
