package pe.edu.lamolina.amauta.controller.academico.encuestaestudiantil.docentemodalidad;

import java.util.List;
import pe.edu.lamolina.model.academico.CicloAcademico;

public class FiltroEncuestaCargaAcademicaDTO {

    private Long departamento;
    private Long docente;
    private String tipoGrado;
    private List<CicloAcademico> cicloAcademicos;
    private Long facultad;
                    
    public boolean hasCiclo(){
        if(this.cicloAcademicos==null){
            return false;
        }
        if(this.cicloAcademicos.isEmpty()){
            return false;
        }
        return true;
    }

    public Long getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Long departamento) {
        this.departamento = departamento;
    }

    public Long getDocente() {
        return docente;
    }

    public void setDocente(Long docente) {
        this.docente = docente;
    }

    public String getTipoGrado() {
        return tipoGrado;
    }

    public void setTipoGrado(String tipoGrado) {
        this.tipoGrado = tipoGrado;
    }

    public List<CicloAcademico> getCicloAcademicos() {
        return cicloAcademicos;
    }

    public void setCicloAcademicos(List<CicloAcademico> cicloAcademicos) {
        this.cicloAcademicos = cicloAcademicos;
    }

    public Long getFacultad() {
        return facultad;
    }

    public void setFacultad(Long facultad) {
        this.facultad = facultad;
    }
    
    

}
