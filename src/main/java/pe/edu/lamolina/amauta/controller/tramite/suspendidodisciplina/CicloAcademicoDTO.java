package pe.edu.lamolina.amauta.controller.tramite.suspendidodisciplina;

import lombok.Data;

@Data
public class CicloAcademicoDTO {
    private Long id;
    private String descripcion;

    public CicloAcademicoDTO() {}

    public CicloAcademicoDTO(Long id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }
}
