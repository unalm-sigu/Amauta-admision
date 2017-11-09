package pe.edu.lamolina.pivot.model.academico;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.zelper.enums.TipoCursoCurriculaEnum;

@Entity
@Table(name = "aca_tipo_curso_curricula")
public class TipoCursoCurricula implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "codigo")
    private String codigo;

    @OneToMany(mappedBy = "tipoCursoCurricula", fetch = FetchType.LAZY)
    private List<CursoCurricula> cursoCurricula;

    @OneToMany(mappedBy = "tipoCursoCurricula", fetch = FetchType.LAZY)
    private List<CursoOpcionalCurricula> cursoOpcionalCurricula;

    @OneToMany(mappedBy = "tipoCursoCurricula", fetch = FetchType.LAZY)
    private List<ResumenPlanCurricular> resumenPlanCurricular;

    public TipoCursoCurricula() {
    }

    public TipoCursoCurricula(Object id) {
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

    public List<CursoCurricula> getCursoCurricula() {
        return cursoCurricula;
    }

    public void setCursoCurricula(List<CursoCurricula> cursoCurricula) {
        this.cursoCurricula = cursoCurricula;
    }

    public List<CursoOpcionalCurricula> getCursoOpcionalCurricula() {
        return cursoOpcionalCurricula;
    }

    public void setCursoOpcionalCurricula(List<CursoOpcionalCurricula> cursoOpcionalCurricula) {
        this.cursoOpcionalCurricula = cursoOpcionalCurricula;
    }

    public List<ResumenPlanCurricular> getResumenPlanCurricular() {
        return resumenPlanCurricular;
    }

    public void setResumenPlanCurricular(List<ResumenPlanCurricular> resumenPlanCurricular) {
        this.resumenPlanCurricular = resumenPlanCurricular;
    }

    public boolean isTieneRequisitos() {
        if (TipoCursoCurriculaEnum.OBL.name().equals(this.getCodigo())
                || TipoCursoCurriculaEnum.GEN.name().equals(this.getCodigo())) {
            return true;
        }
        return false;
    }

    public boolean isTieneCreditoManual() {
        if (TipoCursoCurriculaEnum.OBL.name().equals(this.getCodigo())
                || TipoCursoCurriculaEnum.GEN.name().equals(this.getCodigo())) {
            return true;
        }
        return false;
    }

}
