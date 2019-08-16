package pe.edu.lamolina.pivot.controller.reporte.dto;

import java.util.List;

public class HorarioDTO {

    private String codigo;

    private List<List<HoraDTO>> horarios;

    public HorarioDTO(String codigo) {

        this.codigo = codigo;
    }

    public HorarioDTO() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public List<List<HoraDTO>> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<List<HoraDTO>> horarios) {
        this.horarios = horarios;
    }

}
