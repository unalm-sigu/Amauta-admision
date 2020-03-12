package pe.edu.lamolina.pivot.controller.horariocachimbo.generar;

import java.util.List;
import pe.edu.lamolina.model.academico.Seccion;

public class HorarioExitoso {

    List<Seccion> horario;
    Integer vacantes;
    Integer consumidos;

    public HorarioExitoso(List<Seccion> horario, Integer vacantes, Integer consumidos) {
        this.horario = horario;
        this.vacantes = vacantes;
        this.consumidos = consumidos;
    }

    public List<Seccion> getHorario() {
        return horario;
    }

    public void setHorario(List<Seccion> horario) {
        this.horario = horario;
    }

    public Integer getVacantes() {
        return vacantes;
    }

    public void setVacantes(Integer vacantes) {
        this.vacantes = vacantes;
    }

    public Integer getConsumidos() {
        return consumidos;
    }

    public void setConsumidos(Integer consumidos) {
        this.consumidos = consumidos;
    }

    public Integer getDisponibles() {
        return this.vacantes - this.consumidos;
    }

}
