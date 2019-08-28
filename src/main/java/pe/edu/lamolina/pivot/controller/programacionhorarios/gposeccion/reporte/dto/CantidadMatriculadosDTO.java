package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.reporte.dto;

public class CantidadMatriculadosDTO {

    private String anexoSuperior;
    private String departamentoCurso;
    private String ciclo;
    private String nombreCurso;
    private String nombreSeccion;
    private Long cantidad;

    public CantidadMatriculadosDTO() {
    }

    public CantidadMatriculadosDTO(String ciclo, String anexoSuperior, String departamentoCurso, String nombreCurso, String nombreSeccion, Long cantidad) {
        this.ciclo = ciclo;
        this.nombreCurso = nombreCurso;
        this.nombreSeccion = nombreSeccion;
        this.cantidad = cantidad;
        this.anexoSuperior = anexoSuperior;
        this.departamentoCurso = departamentoCurso;
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

}
