package pe.edu.lamolina.amauta.controller.academico.anexoboletin;

public class AnexoResumen {

    private Long ingresantes;
    private Long departamentos;
    private Long posgrados;
    private Long actividades;

    public AnexoResumen(Long ingresantes, Long departamentos, Long postGrados, Long actividades) {
        this.ingresantes = ingresantes;
        this.departamentos = departamentos;
        this.posgrados = postGrados;
        this.actividades = actividades;
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

    public Long getPosgrados() {
        return posgrados;
    }

    public void setPosgrados(Long posgrados) {
        this.posgrados = posgrados;
    }

    public Long getActividades() {
        return actividades;
    }

    public void setActividades(Long actividades) {
        this.actividades = actividades;
    }

}
