package pe.edu.lamolina.pivot.zelper.bean;

import java.util.List;

public class FormDataBean {
    
    String programado;
    List<VacantesBean> vacantes;
    List<HorarioBean> cruceHorario;

    public String getProgramado() {
        return programado;
    }

    public void setProgramado(String programado) {
        this.programado = programado;
    }

    public List<VacantesBean> getVacantes() {
        return vacantes;
    }

    public void setVacantes(List<VacantesBean> vacantes) {
        this.vacantes = vacantes;
    }

    public List<HorarioBean> getCruceHorario() {
        return cruceHorario;
    }

    public void setCruceHorario(List<HorarioBean> cruceHorario) {
        this.cruceHorario = cruceHorario;
    }
 
    
}
