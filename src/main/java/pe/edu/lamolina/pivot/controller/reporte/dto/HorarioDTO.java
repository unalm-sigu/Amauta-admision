package pe.edu.lamolina.pivot.controller.reporte.dto;

import java.util.List;

public class HorarioDTO {

    private String titulo;

    private List<List<HoraDTO>> horario;

    public HorarioDTO(String titulo) {
        this.titulo = titulo;
    }

    public HorarioDTO() {
    }

    public List<List<HoraDTO>> getHorario() {
        return horario;
    }

    public void setHorario(List<List<HoraDTO>> horario) {
        this.horario = horario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

}
