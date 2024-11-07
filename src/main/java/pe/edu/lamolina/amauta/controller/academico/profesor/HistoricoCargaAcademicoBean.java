package pe.edu.lamolina.amauta.controller.academico.profesor;

public class HistoricoCargaAcademicoBean {

    private String ciclo;
    private String facultad;
    private String departamento;
    private String codDocente;
    private String nombreDocente;
    private String creditosPre;
    private String docenteCiclo;

    public HistoricoCargaAcademicoBean() {
    }

    public HistoricoCargaAcademicoBean(String ciclo, String facultad, String departamento, String codDocente, String nombreDocente, String creditosPre) {
        this.ciclo = ciclo;
        this.facultad = facultad;
        this.departamento = departamento;
        this.codDocente = codDocente;
        this.nombreDocente = nombreDocente;
        this.creditosPre = creditosPre;
    }

    public String getKey() {
        return ciclo + "-" + codDocente;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getCodDocente() {
        return codDocente;
    }

    public void setCodDocente(String codDocente) {
        this.codDocente = codDocente;
    }

    public String getNombreDocente() {
        return nombreDocente;
    }

    public void setNombreDocente(String nombreDocente) {
        this.nombreDocente = nombreDocente;
    }

    public String getCreditosPre() {
        return creditosPre;
    }

    public void setCreditosPre(String creditosPre) {
        this.creditosPre = creditosPre;
    }

}
