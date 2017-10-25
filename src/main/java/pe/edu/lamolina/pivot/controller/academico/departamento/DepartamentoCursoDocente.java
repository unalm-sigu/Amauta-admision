package pe.edu.lamolina.pivot.controller.academico.departamento;

public class DepartamentoCursoDocente {

    private Long id;
    private Long curso;
    private Long docente;

    public DepartamentoCursoDocente() {
    }

    public DepartamentoCursoDocente(Long id, Long curso, Long docente) {
        this.id = id;
        this.curso = curso;
        this.docente = docente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCurso() {
        return curso;
    }

    public void setCurso(Long curso) {
        this.curso = curso;
    }

    public Long getDocente() {
        return docente;
    }

    public void setDocente(Long docente) {
        this.docente = docente;
    }

}
