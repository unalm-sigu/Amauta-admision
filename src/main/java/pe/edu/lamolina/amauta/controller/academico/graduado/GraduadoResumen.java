package pe.edu.lamolina.amauta.controller.academico.graduado;

public class GraduadoResumen {

    private Long bachiller;
    private Long titulo;
    private Long maestria;
    private Long doctorado;

    public GraduadoResumen(Long bachiller, Long titulo, Long maestria, Long doctorado) {
        this.bachiller = bachiller;
        this.titulo = titulo;
        this.maestria = maestria;
        this.doctorado = doctorado;
    }

    public GraduadoResumen() {
        this.bachiller = 0L;
        this.titulo = 0L;
        this.maestria = 0L;
        this.doctorado = 0L;
    }

    public Long getBachiller() {
        return bachiller;
    }

    public void setBachiller(Long bachiller) {
        this.bachiller = bachiller;
    }

    public Long getTitulo() {
        return titulo;
    }

    public void setTitulo(Long titulo) {
        this.titulo = titulo;
    }

    public Long getMaestria() {
        return maestria;
    }

    public void setMaestria(Long maestria) {
        this.maestria = maestria;
    }

    public Long getDoctorado() {
        return doctorado;
    }

    public void setDoctorado(Long doctorado) {
        this.doctorado = doctorado;
    }

}
