package pe.edu.lamolina.amauta.controller.academico.ciclo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class MargenYear {

    private Integer year;
    private Boolean activo;
    private Boolean conDatos;

    public MargenYear(Integer year, Boolean activo) {
        this.year = year;
        this.activo = activo;
        this.conDatos = false;
    }

}
