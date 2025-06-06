package pe.edu.lamolina.amauta.controller.programacionhorarios.resumen;

public class DepartamentoCursosProgramadosDTO {
    private Long idDepartamento;
    private Long cantidadCursos;
    private Long cantidadGrupos;
    private Long activos;
    private Long anulados;
    private Long cancelados;
    private Long fusionados;
    private Long inactivos;
    private Long bloqueados;
    private Long totalSecciones;
    private Long cursosMenos6Alumnos;
    private Long cursosSinDocente;


    public DepartamentoCursosProgramadosDTO() {}

    // Constructor
    public DepartamentoCursosProgramadosDTO(Long idDepartamento, Long cantidadCursos, Long cantidadGrupos, Long activos, Long anulados, Long cancelados, Long fusionados, Long inactivos, Long bloqueados, Long totalSecciones, Long cursosMenos6Alumnos, Long cursosSinDocente) {
        this.idDepartamento = idDepartamento;
        this.cantidadCursos = cantidadCursos;
        this.cantidadGrupos = cantidadGrupos;
        this.activos = activos;
        this.anulados = anulados;
        this.cancelados = cancelados;
        this.fusionados = fusionados;
        this.inactivos = inactivos;
        this.bloqueados = bloqueados;
        this.totalSecciones = totalSecciones;
        this.cursosMenos6Alumnos = cursosMenos6Alumnos;
        this.cursosSinDocente = cursosSinDocente;
    }

    public Long getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Long idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public Long getCantidadCursos() {
        return cantidadCursos;
    }

    public void setCantidadCursos(Long cantidadCursos) {
        this.cantidadCursos = cantidadCursos;
    }

    public Long getCantidadGrupos() {
        return cantidadGrupos;
    }

    public void setCantidadGrupos(Long cantidadGrupos) {
        this.cantidadGrupos = cantidadGrupos;
    }

    public Long getActivos() {
        return activos;
    }

    public void setActivos(Long activos) {
        this.activos = activos;
    }

    public Long getAnulados() {
        return anulados;
    }

    public void setAnulados(Long anulados) {
        this.anulados = anulados;
    }

    public Long getCancelados() {
        return cancelados;
    }

    public void setCancelados(Long cancelados) {
        this.cancelados = cancelados;
    }

    public Long getFusionados() {
        return fusionados;
    }

    public void setFusionados(Long fusionados) {
        this.fusionados = fusionados;
    }

    public Long getInactivos() {
        return inactivos;
    }

    public void setInactivos(Long inactivos) {
        this.inactivos = inactivos;
    }

    public Long getBloqueados() {
        return bloqueados;
    }

    public void setBloqueados(Long bloqueados) {
        this.bloqueados = bloqueados;
    }

    public Long getTotalSecciones() {
        return totalSecciones;
    }

    public void setTotalSecciones(Long totalSecciones) {
        this.totalSecciones = totalSecciones;
    }

    public Long getCursosMenos6Alumnos() {
        return cursosMenos6Alumnos;
    }

    public void setCursosMenos6Alumnos(Long cursosMenos6Alumnos) {
        this.cursosMenos6Alumnos = cursosMenos6Alumnos;
    }

    public Long getCursosSinDocente() {
        return cursosSinDocente;
    }

    public void setCursosSinDocente(Long cursosSinDocente) {
        this.cursosSinDocente = cursosSinDocente;
    }
}
