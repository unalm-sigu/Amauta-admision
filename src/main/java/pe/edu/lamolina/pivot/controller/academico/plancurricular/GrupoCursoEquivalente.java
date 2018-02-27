package pe.edu.lamolina.pivot.controller.academico.plancurricular;

import java.util.List;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.CursoEquivalente;

public class GrupoCursoEquivalente {
    
    private Integer numeroGrupo;
    
    private List<CursoEquivalente> cursoEquivalente;

    private CursoCurricula cursoCurricula;
    
    public Integer getNumeroGrupo() {
        return numeroGrupo;
    }

    public void setNumeroGrupo(Integer numeroGrupo) {
        this.numeroGrupo = numeroGrupo;
    }

    public List<CursoEquivalente> getCursoEquivalente() {
        return cursoEquivalente;
    }

    public void setCursoEquivalente(List<CursoEquivalente> cursoEquivalente) {
        this.cursoEquivalente = cursoEquivalente;
    }

    public CursoCurricula getCursoCurricula() {
        return cursoCurricula;
    }

    public void setCursoCurricula(CursoCurricula cursoCurricula) {
        this.cursoCurricula = cursoCurricula;
    }
    
}