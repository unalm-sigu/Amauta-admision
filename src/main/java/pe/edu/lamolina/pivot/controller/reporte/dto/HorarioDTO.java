package pe.edu.lamolina.pivot.controller.reporte.dto;

import java.util.List;

public class HorarioDTO {

    private String titulo;

    private List<List<HoraDTO>> horarios;

    public HorarioDTO(String titulo) {
        this.titulo = titulo;
    }

    public HorarioDTO() {
    }

    public List<List<HoraDTO>> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<List<HoraDTO>> horarios) {
        this.horarios = horarios;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

}
