package pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.reporte.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CursoDirigidoDTO {

    private String codCurso;
    private String nomCurso;
    private String seccion;
    private String tipo;
    private String grupo;
    private String carga;
    private String aula;
    private String codDocente;
    private String nomDocente;
    private boolean esDirigido;
    private String modalidad;
    private String matriculados;
    private String departamento;
    private String facultad;
    private String cicloAcademico;

    public CursoDirigidoDTO() {
    }

}
