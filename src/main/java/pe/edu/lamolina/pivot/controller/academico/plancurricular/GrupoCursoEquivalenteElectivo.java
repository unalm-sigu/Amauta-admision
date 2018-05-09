package pe.edu.lamolina.pivot.controller.academico.plancurricular;

import java.util.List;
import pe.edu.lamolina.model.academico.CursoEquivalenteElectivo;
import pe.edu.lamolina.model.academico.CursoOpcionalCurricula;

public class GrupoCursoEquivalenteElectivo {

    private Integer numeroGrupo;

    private List<CursoEquivalenteElectivo> cursoEquivalenteElectivo;

    private CursoOpcionalCurricula cursoOpcionalCurricula;

    public Integer getNumeroGrupo() {
        return numeroGrupo;
    }

    public void setNumeroGrupo(Integer numeroGrupo) {
        this.numeroGrupo = numeroGrupo;
    }

    public List<CursoEquivalenteElectivo> getCursoEquivalenteElectivo() {
        return cursoEquivalenteElectivo;
    }

    public void setCursoEquivalenteElectivo(List<CursoEquivalenteElectivo> cursoEquivalenteElectivo) {
        this.cursoEquivalenteElectivo = cursoEquivalenteElectivo;
    }

    public CursoOpcionalCurricula getCursoOpcionalCurricula() {
        return cursoOpcionalCurricula;
    }

    public void setCursoOpcionalCurricula(CursoOpcionalCurricula cursoOpcionalCurricula) {
        this.cursoOpcionalCurricula = cursoOpcionalCurricula;
    }

}
