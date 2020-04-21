package pe.edu.lamolina.amauta.controller.consejeria.consejeros;

public class Aconsejado {

    private Long idCarrera;
    private Long sinRegistro;
    private Long sinConsejeros;
    private Long conConsejeros;
    private Long matriculados;
    private Long noMatriculados;
    private Long matriculadosSinConsejeros;
    private Long matriculadosConConsejeros;
    private Long noMatriculadosSinConsejeros;
    private Long noMatriculadosConConsejeros;
    private Long inhabilitados;

    public Aconsejado() {
        this.sinRegistro = 0L;
        this.sinConsejeros = 0L;
        this.conConsejeros = 0L;
        this.matriculados = 0L;
        this.noMatriculados = 0L;
        this.matriculadosSinConsejeros = 0L;
        this.matriculadosConConsejeros = 0L;
        this.noMatriculadosSinConsejeros = 0L;
        this.noMatriculadosConConsejeros = 0L;
        this.inhabilitados = 0L;
    }

    public Long getIdCarrera() {
        return idCarrera;
    }

    public void setIdCarrera(Long idCarrera) {
        this.idCarrera = idCarrera;
    }

    public Long getSinRegistro() {
        return sinRegistro;
    }

    public void setSinRegistro(Long sinRegistro) {
        this.sinRegistro = sinRegistro;
    }

    public Long getSinConsejeros() {
        return sinConsejeros;
    }

    public void setSinConsejeros(Long sinConsejeros) {
        this.sinConsejeros = sinConsejeros;
    }

    public Long getConConsejeros() {
        return conConsejeros;
    }

    public void setConConsejeros(Long conConsejeros) {
        this.conConsejeros = conConsejeros;
    }

    public Long getMatriculados() {
        return matriculados;
    }

    public void setMatriculados(Long matriculados) {
        this.matriculados = matriculados;
    }

    public Long getNoMatriculados() {
        return noMatriculados;
    }

    public void setNoMatriculados(Long noMatriculados) {
        this.noMatriculados = noMatriculados;
    }

    public Long getMatriculadosSinConsejeros() {
        return matriculadosSinConsejeros;
    }

    public void setMatriculadosSinConsejeros(Long matriculadosSinConsejeros) {
        this.matriculadosSinConsejeros = matriculadosSinConsejeros;
    }

    public Long getMatriculadosConConsejeros() {
        return matriculadosConConsejeros;
    }

    public void setMatriculadosConConsejeros(Long matriculadosConConsejeros) {
        this.matriculadosConConsejeros = matriculadosConConsejeros;
    }

    public Long getNoMatriculadosSinConsejeros() {
        return noMatriculadosSinConsejeros;
    }

    public void setNoMatriculadosSinConsejeros(Long noMatriculadosSinConsejeros) {
        this.noMatriculadosSinConsejeros = noMatriculadosSinConsejeros;
    }

    public Long getNoMatriculadosConConsejeros() {
        return noMatriculadosConConsejeros;
    }

    public void setNoMatriculadosConConsejeros(Long noMatriculadosConConsejeros) {
        this.noMatriculadosConConsejeros = noMatriculadosConConsejeros;
    }

    public Long getInhabilitados() {
        return inhabilitados;
    }

    public void setInhabilitados(Long inhabilitados) {
        this.inhabilitados = inhabilitados;
    }

}
