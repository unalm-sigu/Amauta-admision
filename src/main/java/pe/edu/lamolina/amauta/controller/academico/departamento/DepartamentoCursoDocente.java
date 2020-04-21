package pe.edu.lamolina.amauta.controller.academico.departamento;

public class DepartamentoCursoDocente {

    private Long id;
    private Long cursoActivos;
    private Long cursoInactivos;
    private Long docenteActivos;
    private Long docenteInactivos;

    public DepartamentoCursoDocente() {
    }

    public DepartamentoCursoDocente(Long id, Long cursoActivos, Long cursoInactivos, Long docenteActivos, Long docenteInactivos) {
        this.id = id;
        this.cursoActivos = cursoActivos;
        this.cursoInactivos = cursoInactivos;
        this.docenteActivos = docenteActivos;
        this.docenteInactivos = docenteInactivos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCursoActivos() {
        return cursoActivos;
    }

    public void setCursoActivos(Long cursoActivos) {
        this.cursoActivos = cursoActivos;
    }

    public Long getCursoInactivos() {
        return cursoInactivos;
    }

    public void setCursoInactivos(Long cursoInactivos) {
        this.cursoInactivos = cursoInactivos;
    }

    public Long getDocenteActivos() {
        return docenteActivos;
    }

    public void setDocenteActivos(Long docenteActivos) {
        this.docenteActivos = docenteActivos;
    }

    public Long getDocenteInactivos() {
        return docenteInactivos;
    }

    public void setDocenteInactivos(Long docenteInactivos) {
        this.docenteInactivos = docenteInactivos;
    }

}
