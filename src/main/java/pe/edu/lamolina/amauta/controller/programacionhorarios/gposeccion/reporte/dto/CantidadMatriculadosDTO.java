package pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.reporte.dto;

public class CantidadMatriculadosDTO {

    private Long cursoId;
    private String anexoSuperior;
    private String anexo;
    private String departamentoCurso;
    private String ciclo;
    private String codigoCurso;
    private String nombreCurso;
    private String nombreSeccion;
    private String codigoDocente;
    private String nombreDocente;
    private Long cantidad;

    public CantidadMatriculadosDTO() {
    }

    public CantidadMatriculadosDTO(Long cursoId, Long cantidad) {
        this.cursoId = cursoId;
        this.cantidad = cantidad;
    }

    public CantidadMatriculadosDTO(
            String ciclo,
            String anexoSuperior, String anexo,
            String departamentoCurso, String codigoCurso, String nombreCurso,
            String codigoDocente, String nombreDocente,
            String seccion, Long cantidad) {

        this.ciclo = ciclo;
        this.codigoCurso = codigoCurso;
        this.nombreCurso = nombreCurso;
        this.nombreSeccion = seccion;
        this.cantidad = cantidad;
        this.anexoSuperior = anexoSuperior;
        this.anexo = anexo;
        this.departamentoCurso = departamentoCurso;
        this.codigoDocente = codigoDocente;
        this.nombreDocente = nombreDocente;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public void setNombreCurso(String nombreCurso) {
        this.nombreCurso = nombreCurso;
    }

    public String getNombreSeccion() {
        return nombreSeccion;
    }

    public void setNombreSeccion(String nombreSeccion) {
        this.nombreSeccion = nombreSeccion;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }

    public String getAnexoSuperior() {
        return anexoSuperior;
    }

    public void setAnexoSuperior(String anexoSuperior) {
        this.anexoSuperior = anexoSuperior;
    }

    public String getDepartamentoCurso() {
        return departamentoCurso;
    }

    public void setDepartamentoCurso(String departamentoCurso) {
        this.departamentoCurso = departamentoCurso;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public String getAnexo() {
        return anexo;
    }

    public void setAnexo(String anexo) {
        this.anexo = anexo;
    }

    public String getCodigoCurso() {
        return codigoCurso;
    }

    public void setCodigoCurso(String codigoCurso) {
        this.codigoCurso = codigoCurso;
    }

    public String getCodigoDocente() {
        return codigoDocente;
    }

    public void setCodigoDocente(String codigoDocente) {
        this.codigoDocente = codigoDocente;
    }

    public String getNombreDocente() {
        return nombreDocente;
    }

    public void setNombreDocente(String nombreDocente) {
        this.nombreDocente = nombreDocente;
    }

}
