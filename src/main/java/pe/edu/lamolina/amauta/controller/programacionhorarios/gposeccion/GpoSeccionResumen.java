package pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion;

public class GpoSeccionResumen {

    private Long ingresantes;
    private Long departamentos;
    private Long postGrados;
    private Long actividades;
    private Long activos;
    private Long inactivos;
    private Long semestrales;
    private Long modulares;

    public GpoSeccionResumen(
            Long ingresantes, Long departamentos, Long postGrados, Long actividades,
            Long activos, Long inactivos, Long semestrales, Long modulares) {

        this.ingresantes = ingresantes;
        this.departamentos = departamentos;
        this.postGrados = postGrados;
        this.actividades = actividades;
        this.activos = activos;
        this.inactivos = inactivos;
        this.semestrales = semestrales;
        this.modulares = modulares;
    }

    public Long getIngresantes() {
        return ingresantes;
    }

    public void setIngresantes(Long ingresantes) {
        this.ingresantes = ingresantes;
    }

    public Long getDepartamentos() {
        return departamentos;
    }

    public void setDepartamentos(Long departamentos) {
        this.departamentos = departamentos;
    }

    public Long getPostGrados() {
        return postGrados;
    }

    public void setPostGrados(Long postGrados) {
        this.postGrados = postGrados;
    }

    public Long getActividades() {
        return actividades;
    }

    public void setActividades(Long actividades) {
        this.actividades = actividades;
    }

    public Long getActivos() {
        return activos;
    }

    public void setActivos(Long activos) {
        this.activos = activos;
    }

    public Long getInactivos() {
        return inactivos;
    }

    public void setInactivos(Long inactivos) {
        this.inactivos = inactivos;
    }

    public Long getSemestrales() {
        return semestrales;
    }

    public void setSemestrales(Long semestrales) {
        this.semestrales = semestrales;
    }

    public Long getModulares() {
        return modulares;
    }

    public void setModulares(Long modulares) {
        this.modulares = modulares;
    }

}
